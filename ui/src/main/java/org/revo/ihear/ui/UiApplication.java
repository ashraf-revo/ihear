package org.revo.ihear.ui;

import org.revo.ihear.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWebFluxSecurity
@ComponentScan(basePackages = {"org.revo.ihear.service.auth", "org.revo.ihear.ui"})
public class UiApplication {
    private static final List<String> services = Arrays.asList("/auth/**", "/pi/**", "/echo/**", "/login");
    private final RequestPredicate requestPredicate = serverRequest -> services.stream().map(it -> new PathPatternParser().parse(it))
            .noneMatch(it -> it.matches(serverRequest.exchange().getRequest().getPath().pathWithinApplication())) && !serverRequest.path().contains(".");

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> routes(@Value("classpath:/static/index.html") Resource index, AuthService authService, OAuth2ClientProperties oAuth2ClientProperties, CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository) {
        return route(requestPredicate, serverRequest -> ok().contentType(MediaType.TEXT_HTML).bodyValue(index))
                .andRoute(GET("/login"), serverRequest -> authService.currentOAuth2User().defaultIfEmpty("")
                        .zipWith(generateToken(cookieServerCsrfTokenRepository, serverRequest.exchange()))
                        .map(Tuple2::getT1)
                        .flatMap(it -> {
                            String red = "/";
                            if (it.isEmpty())
                                red = "/oauth2/authorization/" + serverRequest.queryParam("app").orElseGet(() -> oAuth2ClientProperties.getRegistration().entrySet().stream().findAny().map(Map.Entry::getKey).get());
                            return ServerResponse.temporaryRedirect(URI.create(red)).build();
                        }));
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository) {
        return http
                .exceptionHandling().authenticationEntryPoint((exchange, e) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return generateToken(cookieServerCsrfTokenRepository, exchange).flatMap(it -> exchange.getResponse().setComplete());
                }).and()
                .authorizeExchange()
                .anyExchange().permitAll()
                .and().oauth2Login()
                .and().logout()
                .and().csrf().csrfTokenRepository(cookieServerCsrfTokenRepository)
                .requireCsrfProtectionMatcher(exchange -> (exchange.getRequest().getMethod() != HttpMethod.GET && !exchange.getRequest().getPath().toString().startsWith("/auth")) ? ServerWebExchangeMatcher.MatchResult.match() : ServerWebExchangeMatcher.MatchResult.notMatch())
                .and().build();
    }

    @Bean
    public CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository() {
        return CookieServerCsrfTokenRepository.withHttpOnlyFalse();
    }

    private static Mono<CsrfToken> generateToken(CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository, ServerWebExchange exchange) {
        return cookieServerCsrfTokenRepository.loadToken(exchange)
                .switchIfEmpty(cookieServerCsrfTokenRepository.generateToken(exchange)
                        .delayUntil(token -> cookieServerCsrfTokenRepository.saveToken(exchange, token)))
                .doOnNext(it -> exchange.getAttributes().put(CsrfToken.class.getName(), it));
    }
}
