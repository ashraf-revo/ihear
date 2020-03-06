package org.revo.ihear.ui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

import static org.revo.ihear.ui.util.Utils.generateToken;

@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository, @Value("classpath:/static/index.html") Resource index) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((exchange, e) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return generateToken(cookieServerCsrfTokenRepository, exchange).flatMap(it -> exchange.getResponse().setComplete());
                }).and()
                .authorizeExchange()
                .anyExchange().permitAll()
                .and().oauth2Login().authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/403"))
                .and().logout()
                .and().csrf().csrfTokenRepository(cookieServerCsrfTokenRepository)
                .requireCsrfProtectionMatcher(exchange -> (exchange.getRequest().getMethod() != HttpMethod.GET && !exchange.getRequest().getPath().toString().startsWith("/auth")) ? ServerWebExchangeMatcher.MatchResult.match() : ServerWebExchangeMatcher.MatchResult.notMatch())
                .and().build();
    }

    @Bean
    public CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository() {
        return CookieServerCsrfTokenRepository.withHttpOnlyFalse();
    }

}
