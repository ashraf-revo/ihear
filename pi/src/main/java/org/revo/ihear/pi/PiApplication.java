package org.revo.ihear.pi;

import org.revo.ihear.entites.domain.Stream;
import org.revo.ihear.entites.service.stream.StreamService;
import org.revo.ihear.pi.config.WebSocketTemplate;
import org.revo.ihear.pi.config.domain.WSMessage;
import org.revo.ihear.pi.domain.Device;
import org.revo.ihear.pi.domain.Schema;
import org.revo.ihear.pi.service.DeviceService;
import org.revo.ihear.pi.service.SchemaService;
import org.revo.ihear.service.auth.AuthService;
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
@ComponentScan(basePackages = {"org.revo.ihear.entites.config", "org.revo.ihear.service.auth",
        "org.revo.ihear.entites.service.stream", "org.revo.ihear.entites.repository.stream", "org.revo.ihear.pi"})
@EnableMongoRepositories(basePackages = {"org.revo.ihear.entites.repository.stream", "org.revo.ihear.pi"})
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
    public RouterFunction<ServerResponse> routes(AuthService authService,
                                                 DeviceService deviceService,
                                                 StreamService streamService,
                                                 SchemaService schemaService,
                                                 WebSocketTemplate webSocketTemplate) {
        return route(POST("/device"), serverRequest ->
                ok().body(serverRequest.bodyToMono(Device.class)
                        .filterWhen(it -> {
                            if (it.getId() != null)
                                return authService.currentJwtUserId()
                                        .map(user -> deviceService.findOneById(it.getId())
                                                .map(device -> device.getCreatedBy().equals(user)).orElse(false))
                                        .filter(user -> user).defaultIfEmpty(false);
                            else
                                return Mono.just(true);
                        })
                        .flatMap(it -> authService.currentJwtUserId().map(it::setCreatedBy))
                        .flatMap(deviceService::save), Device.class))
                .andRoute(GET("/device/{id}"), serverRequest ->
                        ok().body(authService.currentJwtUserId()
                                .flatMap(it -> deviceService.findOneById(serverRequest.pathVariable("id"))
                                        .filter(its -> it.equals(its.getCreatedBy()))
                                        .map(Mono::just).orElseGet(Mono::empty)), Device.class))
                .andRoute(GET("/device"), serverRequest ->
                        ok().body(authService.currentJwtUserId()
                                .flatMapMany(it -> Flux.fromIterable(deviceService.findAll(it))), Device.class))
                .andRoute(POST("/stream"), serverRequest ->
                        ok().body(serverRequest.bodyToMono(Stream.class)
                                .filterWhen(it -> {
                                    if (it.getId() != null)
                                        return authService.currentJwtUserId()
                                                .map(user -> streamService.findOneById(it.getId())
                                                        .map(stream -> stream.getCreatedBy().equals(user)).orElse(false))
                                                .filter(user -> user).defaultIfEmpty(false);
                                    else
                                        return Mono.just(true);
                                })
                                .flatMap(it -> authService.currentJwtUserId().map(it::setCreatedBy))
                                .map(streamService::save), Stream.class))
                .andRoute(GET("/stream/{id}"), serverRequest ->
                        ok().body(authService.currentJwtUserId()
                                .flatMap(it -> streamService.findOneById(serverRequest.pathVariable("id"))
                                        .filter(its -> it.equals(its.getCreatedBy()))
                                        .map(Mono::just).orElseGet(Mono::empty)), Stream.class))
                .andRoute(GET("/stream"), serverRequest ->
                        ok().body(authService.currentJwtUserId()
                                .flatMapMany(it -> Flux.fromIterable(streamService.findAll(it))), Stream.class))
                .andRoute(POST("/schema"), serverRequest ->
                        ok().body(serverRequest.bodyToMono(Schema.class)
                                .zipWith(authService.currentJwtUserId(), (schema, createdBy) -> {
                                    schema.setCreatedBy(createdBy);
                                    if (schema.getParentId() == null)
                                        schema.setParentId(schema.getId());
                                    schema.setId(null);
                                    return schema;
                                }).map(schema -> schemaService.save(schema)), Schema.class)
                )
                .andRoute(GET("/schema/{id}"), serverRequest ->
                        ok().body(schemaService.findOneById(serverRequest.pathVariable("id")).map(Mono::just)
                                .orElseGet(Mono::empty), Schema.class))
                .andRoute(GET("/schema"), serverRequest ->
                        ok().body(Flux.fromIterable(schemaService.findAll()), Schema.class))
                .andRoute(GET("/user"), serverRequest ->
                        ok().body(authService.currentJwtUserId()
                                        .map(it ->
                                                "user " + it + "  from " + serverRequest.exchange().getRequest().getRemoteAddress())
                                , String.class))
                .andRoute(POST("/notify/{deviceId}"), serverRequest ->
                        ok().body(
                                authService.currentJwtUserId()
                                        .filter(it ->
                                                deviceService.findOneById(serverRequest.pathVariable("deviceId")).
                                                        map(its ->
                                                                it.contains(its.getCreatedBy())).orElse(false))
                                        .flatMap(userId -> serverRequest.bodyToMono(WSMessage.class)
                                                .map(it -> {
                                                    it.setFrom(userId).setTo("/echo/" + it.getTo());
                                                    return it;
                                                }).map(it -> MessageBuilder.withPayload(it).build())
                                        )
                                        .doOnNext(it -> webSocketTemplate.send(it)).then(), Void.class));
    }
}
