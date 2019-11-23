package org.revo.ihear.pi;

import org.revo.base.domain.Stream;
import org.revo.base.service.auth.AuthService;
import org.revo.base.service.stream.StreamService;
import org.revo.ihear.pi.config.WebSocketTemplate;
import org.revo.ihear.pi.config.domain.WSMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWebFluxSecurity
@ComponentScan(basePackages = {"org.revo.base.config", "org.revo.base.service.auth", "org.revo.base.service.stream", "org.revo.base.repository.stream", "org.revo.ihear.pi"})
@EnableMongoRepositories(basePackages = {"org.revo.base.repository.stream", "org.revo.ihear.pi"})
@EnableMongoAuditing
public class PiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PiApplication.class, args);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .matchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .pathMatchers("/echo").authenticated()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(new JwtAuthenticationConverter()))
                .and().and().build();
    }


    @Bean
    public RouterFunction<ServerResponse> routes(AuthService authService, StreamService streamService, WebSocketTemplate webSocketTemplate) {
        return route(POST("/"), serverRequest -> ok().body(serverRequest.bodyToMono(Stream.class).flatMap(it -> authService.currentJwtUserId().map(it::setCreateBy)).map(streamService::save), Stream.class))
                .andRoute(GET("/"), serverRequest -> ok().body(authService.currentJwtUserId().flatMapMany(it -> Flux.fromIterable(streamService.findAll(it))), Stream.class))
                .andRoute(GET("/user"), serverRequest -> ok().body(authService.currentJwtUserId().map(it -> "user " + it + "  from " + serverRequest.exchange().getRequest().getRemoteAddress()), String.class))
                .andRoute(POST("/notify/{streamId}"), serverRequest -> {
                    Mono<Void> notiy = authService.currentJwtUserId()
                            .filter(it -> streamService.findOneById(serverRequest.pathVariable("streamId")).
                                    map(its -> it.equals(its.getCreateBy())).orElse(false))
                            .flatMap(userId -> serverRequest.bodyToMono(WSMessage.class)
                                    .map(it -> {
                                        it.setFrom(userId);
                                        it.setTo("/echo/" + userId + "/" + serverRequest.pathVariable("streamId"));
                                        return it;
                                    })
                                    .map(it -> MessageBuilder.withPayload(it).build()))
                            .doOnNext(webSocketTemplate::send).then();
                    return ok().body(notiy, Void.class);
                });
    }
}
