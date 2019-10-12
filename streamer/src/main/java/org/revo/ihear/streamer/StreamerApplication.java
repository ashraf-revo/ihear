package org.revo.ihear.streamer;

import org.revo.base.service.auth.AuthService;
import org.revo.base.service.stream.StreamService;
import org.revo.ihear.livepoll.rtsp.rtp.base.NALU;
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
import java.util.Collections;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.revo.ihear.livepoll.rtsp.rtp.base.NALU.getRaw;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
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
                .pathMatchers("/h264/*").permitAll()
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
        synchronized (processor) {
            processor.onNext(message);
        }
    }

    @Bean
    public RouterFunction<ServerResponse> function(AuthService authService, StreamService streamService, Flux<Message<byte[]>> stream) {
        DefaultDataBufferFactory ddbf = new DefaultDataBufferFactory();
        return route(GET("/h264/{id}"), serverRequest -> ok()
                .header("Content-Type", "video/h264")
                .body(fromIterable(streamService.findOneById(serverRequest.pathVariable("id"))
                        .map(it -> asList(getRaw(it.getSps()), getRaw(it.getPps()), getRaw(it.getSei()), getRaw(it.getIdr()))).orElse(Collections.emptyList()))
                        .filter(it -> it.length > 4)
                        .mergeWith(stream
                                .filter(it -> Objects.equals(it.getHeaders().get("streamId"), serverRequest.pathVariable("id")))
                                .filter(it -> it.getPayload().length > 0).map(Message::getPayload).map(NALU::getRaw))
//                        .buffer(Duration.ofMillis(350))
//                        .flatMap(it -> Flux.fromIterable(it.stream().sorted().collect(Collectors.toList())))
                        .map(ddbf::wrap), DataBuffer.class))
                .andRoute(GET("/user"), serverRequest -> ok().body(authService.currentJwtUserId().map(it -> "user " + it + "  from " + serverRequest.exchange().getRequest().getRemoteAddress()), String.class));
    }
}
