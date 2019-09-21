package org.revo.ihear.pi;

import org.revo.base.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@ComponentScan(basePackages = {"org.revo.base", "org.revo.ihear.pi"})
@EnableMongoRepositories(basePackages = {"org.revo.base", "org.revo.ihear.pi"})
@EnableDiscoveryClient
@EnableWebFluxSecurity
@EnableBinding(Source.class)
public class PiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PiApplication.class, args);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .matchers(EndpointRequest.toAnyEndpoint()).permitAll()
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


    @Bean
    public RouterFunction<ServerResponse> routes(UserService userService) {
        return route().GET("/", serverRequest -> ServerResponse.ok().body(userService.current().map(it -> "user " + it + "  from " + serverRequest.exchange().getRequest().getRemoteAddress()), String.class)).build();
    }
}
