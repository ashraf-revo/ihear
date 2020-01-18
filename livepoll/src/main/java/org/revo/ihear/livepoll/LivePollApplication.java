package org.revo.ihear.livepoll;

import org.revo.ihear.livepoll.config.PiSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"org.revo.ihear.entites.config", "org.revo.ihear.service.auth", "org.revo.ihear.livepoll"})
@EnableBinding(PiSource.class)
public class LivePollApplication {
    public static void main(String[] args) {
        SpringApplication.run(LivePollApplication.class, args);
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
}
