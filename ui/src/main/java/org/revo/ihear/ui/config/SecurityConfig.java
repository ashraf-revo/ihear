package org.revo.ihear.ui.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.web.reactive.function.client.WebClient;

import static org.revo.ihear.ui.util.Utils.generateToken;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult.match;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult.notMatch;

@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    @Profile("cloud")
    @LoadBalanced
    public WebClient.Builder cloudBuilder() {
        return WebClient.builder();
    }


    @Profile("cloud")
    @Bean
    public WebClientReactiveAuthorizationCodeTokenResponseClient webClientReactiveAuthorizationCodeTokenResponseClient(WebClient.Builder cloudBuilder) {
        WebClientReactiveAuthorizationCodeTokenResponseClient webClientReactiveAuthorizationCodeTokenResponseClient = new WebClientReactiveAuthorizationCodeTokenResponseClient();
        webClientReactiveAuthorizationCodeTokenResponseClient.setWebClient(cloudBuilder.build());
        return webClientReactiveAuthorizationCodeTokenResponseClient;
    }

    @Profile("cloud")
    @Bean
    public DefaultReactiveOAuth2UserService defaultReactiveOAuth2UserService(WebClient.Builder cloudBuilder) {
        DefaultReactiveOAuth2UserService defaultReactiveOAuth2UserService = new DefaultReactiveOAuth2UserService();
        defaultReactiveOAuth2UserService.setWebClient(cloudBuilder.build());
        return defaultReactiveOAuth2UserService;
    }

    @Bean
    public ServerRequestCache serverRequestCache() {
        return new ServerRequestCacheImpl();
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ServerRequestCache serverRequestCache
            , CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository) {
        RedirectServerAuthenticationSuccessHandler redirectServerAuthenticationSuccessHandler = new RedirectServerAuthenticationSuccessHandler("/home");
        redirectServerAuthenticationSuccessHandler.setRequestCache(serverRequestCache);
        return http
                .exceptionHandling()
                .authenticationEntryPoint((exchange, e) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return generateToken(cookieServerCsrfTokenRepository, exchange).flatMap(it -> exchange.getResponse().setComplete());
                }).and()
                .authorizeExchange()
                .anyExchange().permitAll()
                .and().oauth2Login().authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/403"))
                .authenticationSuccessHandler(redirectServerAuthenticationSuccessHandler)
                .and().logout()
                .and().csrf().csrfTokenRepository(cookieServerCsrfTokenRepository)
                .requireCsrfProtectionMatcher(exchange -> {
                    ServerHttpRequest request = exchange.getRequest();
                    return (request.getMethod() != HttpMethod.GET && !request.getPath().toString().startsWith("/auth"))
                            ? match() : notMatch();
                })
                .and().build();
    }

    @Bean
    public CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository() {
        return CookieServerCsrfTokenRepository.withHttpOnlyFalse();
    }

}
