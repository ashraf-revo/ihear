package org.revo.ihear.streamer;

import org.revo.base.service.UserService;
import org.revo.ihear.streamer.codec.base.NALU;
import org.revo.ihear.streamer.codec.base.Raw;
import org.revo.ihear.streamer.codec.base.RtpPkt;
import org.revo.ihear.streamer.codec.rtp.Encoder;
import org.revo.ihear.streamer.codec.rtp.RtpNaluEncoder;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
@ComponentScan(basePackages = {"org.revo.base", "org.revo.ihear.streamer"})
@EnableMongoRepositories(basePackages = {"org.revo.base", "org.revo.ihear.streamer"})
@EnableDiscoveryClient
@EnableWebFluxSecurity
@EnableBinding(Sink.class)
public class StreamerApplication {
    private Encoder<RtpPkt, NALU> rtpPktToNalu = new RtpNaluEncoder();
    @Autowired
    private FluxProcessor<RtpPkt, RtpPkt> processor;

    public static void main(String[] args) {
        SpringApplication.run(StreamerApplication.class, args);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .matchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .pathMatchers("/h264").permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(new JwtAuthenticationConverter() {
                    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
                        return createAuthorityList(((ArrayList<String>) jwt.getClaims().get("authorities")).stream().toArray(String[]::new));
                    }
                }))
                .and().and().build();
    }

    @StreamListener(Sink.INPUT)
    public void handle(Message<byte[]> message) {
        System.out.println(message.getPayload().length);
        synchronized (processor) {
            processor.onNext(new RtpPkt(message.getPayload()));
        }
    }

    @Bean
    public RouterFunction<ServerResponse> function(UserService userService, Flux<RtpPkt> stream) {
        DefaultDataBufferFactory ddbf = new DefaultDataBufferFactory();
        return route(GET("/h264"), serverRequest -> ok()
                .header("Content-Type", "video/h264")
                .body(stream.
                        map(it -> rtpPktToNalu.encode(it))
                        .flatMap(Flux::fromIterable)
                        .map(Raw::getRaw)
                        .filter(it -> it.length > 0)
                        .map(ddbf::wrap), DataBuffer.class)).andRoute(GET("/"), serverRequest -> ok().body(userService.current().map(it -> "user " + it + "  from " + serverRequest.exchange().getRequest().getRemoteAddress()), String.class));
    }

}
