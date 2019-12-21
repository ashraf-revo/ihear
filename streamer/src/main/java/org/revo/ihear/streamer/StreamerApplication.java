package org.revo.ihear.streamer;

import org.revo.base.service.auth.AuthService;
import org.revo.base.service.stream.StreamService;
import org.revo.ihear.livepoll.rtsp.rtp.base.AdtsFrame;
import org.revo.ihear.livepoll.rtsp.rtp.base.NALU;
import org.revo.ihear.streamer.config.PiSink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.revo.ihear.livepoll.rtsp.d.MediaType.AUDIO;
import static org.revo.ihear.livepoll.rtsp.d.MediaType.VIDEO;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWebFluxSecurity
@ComponentScan(basePackages = {"org.revo.base.config", "org.revo.base.service.auth", "org.revo.base.service.stream", "org.revo.base.repository.stream", "org.revo.ihear.streamer"})
@EnableMongoRepositories(basePackages = {"org.revo.base.repository.stream", "org.revo.ihear.streamer"})
@EnableBinding(PiSink.class)
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
//                .anyExchange().authenticated()
                .anyExchange().permitAll()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(new JwtAuthenticationConverter()))
                .and().and().build();
    }

    @StreamListener(PiSink.INPUT1)
    public synchronized void handleRtp(Message<byte[]> message) {
        processor.onNext(message);
    }

    @Autowired
    private StreamService streamService;

    @StreamListener(PiSink.INPUT2)
    public synchronized void handleSdp(Message<byte[]> message) {
        NALU.NaluHeader naluHeader = NALU.NaluHeader.read(message.getPayload()[0]);
        if (naluHeader.getTYPE() == 7) {
            streamService.setSps(message.getHeaders().get("streamId").toString(), message.getPayload());
        }
        if (naluHeader.getTYPE() == 8) {
            streamService.setPps(message.getHeaders().get("streamId").toString(), message.getPayload());
        }
    }

    @Bean
    public RouterFunction<ServerResponse> function(AuthService authService, Flux<Message<byte[]>> stream) {
        DefaultDataBufferFactory ddbf = new DefaultDataBufferFactory();
        return route(GET("/video/{id}"), serverRequest -> ok()
                .header("Content-Type", "video/h264")
                .body(Flux.fromIterable(streamService.findOneById(serverRequest.pathVariable("id")).
                                map(it -> Arrays.asList(it.getVideoContent().getSps(), it.getVideoContent().getPps()))
                                .orElse(Collections.emptyList()))
                                .mergeWith(stream.filter(it -> VIDEO.name().equals(it.getHeaders().get("type")))
                                        .filter(it -> Objects.equals(it.getHeaders().get("streamId"), serverRequest.pathVariable("id")))
                                        .filter(it -> it.getPayload().length > 0)
                                        .map(Message::getPayload))
                                .map(NALU::getRaw)
                                .map(ddbf::wrap)
                        , DataBuffer.class)).andRoute(GET("/audio/{id}"), serverRequest -> ok()
                .header("Content-Type", "audio/aac")
                .body(stream.filter(it -> AUDIO.name().equals(it.getHeaders().get("type")))
                                .filter(it -> Objects.equals(it.getHeaders().get("streamId"), serverRequest.pathVariable("id")))
                                .filter(it -> it.getPayload().length > 0)
                                .map(Message::getPayload)
                                .map(AdtsFrame::getRaw)
                                .map(ddbf::wrap)
                        , DataBuffer.class))
                .andRoute(GET("/user"), serverRequest -> ok().body(authService.currentJwtUserId()
                        .map(it -> "user " + it + "  from " + serverRequest.exchange().getRequest().getRemoteAddress()), String.class));
    }
}
