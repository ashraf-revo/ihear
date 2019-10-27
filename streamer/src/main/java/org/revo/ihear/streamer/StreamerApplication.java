package org.revo.ihear.streamer;

import org.revo.base.service.auth.AuthService;
import org.revo.base.service.stream.StreamService;
import org.revo.ihear.livepoll.rtsp.rtp.Encoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpAdtsFrameEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpNaluEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.base.AdtsFrame;
import org.revo.ihear.livepoll.rtsp.rtp.base.NALU;
import org.revo.ihear.livepoll.rtsp.rtp.base.RtpPkt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.messaging.Message;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Flux.fromIterable;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWebFluxSecurity
@ComponentScan(basePackages = {"org.revo.base.config", "org.revo.base.service.auth", "org.revo.base.service.stream", "org.revo.base.repository.stream", "org.revo.ihear.streamer"})
@EnableMongoRepositories(basePackages = {"org.revo.base.repository.stream", "org.revo.ihear.streamer"})
@EnableBinding(Sink.class)
public class StreamerApplication {
    @Autowired
    private FluxProcessor<Message<byte[]>, Message<byte[]>> processor;

    public static void main(String[] args) {
        SpringApplication.run(StreamerApplication.class, args);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        return http
                .authorizeExchange()
                .matchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .anyExchange().authenticated()
//                .anyExchange().permitAll()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(new JwtAuthenticationConverter()))
                .and().and().build();
    }

    @StreamListener(Sink.INPUT)
    public synchronized void handle(Message<byte[]> message) {
        processor.onNext(message);
    }

    @Bean
    public RouterFunction<ServerResponse> function(AuthService authService, StreamService streamService, Flux<Message<byte[]>> stream) {
        DefaultDataBufferFactory ddbf = new DefaultDataBufferFactory();
        return route(GET("/video/{id}"), serverRequest -> {
            final Encoder<RtpPkt, NALU> rtpNaluEncoder = new RtpNaluEncoder();
            return ok()
                    .header("Content-Type", "video/h264")
                    .body(fromIterable(streamService.findOneById(serverRequest.pathVariable("id"))
                                    .map(it -> Stream.of(it.getVideoContent().getSps(), it.getVideoContent().getPps(), it.getVideoContent().getIdr(), it.getVideoContent().getSei()).filter(itb -> itb != null && itb.length > 0).map(NALU::getRaw).collect(Collectors.toList())).orElse(Collections.emptyList()))
                                    .mergeWith(stream.filter(it -> "Video".equals(it.getHeaders().get("type"))).filter(it -> Objects.equals(it.getHeaders().get("streamId"), serverRequest.pathVariable("id"))).filter(it -> it.getPayload().length > 0)
                                            .map(Message::getPayload)
                                            .map(it -> new RtpPkt(0, it))
                                            .map(rtpNaluEncoder::encode)
                                            .flatMap(Flux::fromIterable)
                                            .map(NALU::getRaw)
                                    )
                                    .map(ddbf::wrap)
                            , DataBuffer.class);
        }).andRoute(GET("/audio/{id}"), serverRequest -> {
            Encoder<RtpPkt, AdtsFrame> rtpAdtsFrameEncoder = new RtpAdtsFrameEncoder();
            return ok()
                    .header("Content-Type", "audio/aac")
                    .body(stream
                                    .filter(it -> "Audio".equals(it.getHeaders().get("type"))).filter(it -> Objects.equals(it.getHeaders().get("streamId"), serverRequest.pathVariable("id")))
                                    .filter(it -> it.getPayload().length > 0)
                                    .map(Message::getPayload)
                                    .map(it -> new RtpPkt(0, it))
                                    .map(rtpAdtsFrameEncoder::encode)
                                    .flatMap(Flux::fromIterable)
                                    .map(AdtsFrame::getRaw)
                                    .map(ddbf::wrap)
                            , DataBuffer.class);
        })
                .andRoute(GET("/user"), serverRequest -> ok().body(authService.currentJwtUserId().map(it -> "user " + it + "  from " + serverRequest.exchange().getRequest().getRemoteAddress()), String.class));
    }
}
