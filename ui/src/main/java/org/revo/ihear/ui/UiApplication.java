package org.revo.ihear.ui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ExchangeFunctions;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@EnableDiscoveryClient
public class UiApplication {
    private static final List<String> services = List.of("/auth/**", "/pi/**", "/login");
    private final RequestPredicate requestPredicate = serverRequest -> services.stream().map(it -> new PathPatternParser().parse(it)).noneMatch(it -> it.matches(serverRequest.exchange().getRequest().getPath().pathWithinApplication())) && !serverRequest.path().contains(".");

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> routes(@Value("classpath:/static/index.html") Resource index, UserService userService) {
        return route(requestPredicate, serverRequest -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).syncBody(index))
                .andRoute(RequestPredicates.GET("/login"), serverRequest -> userService.current().defaultIfEmpty("").flatMap(it -> {
                    String red = "/";
                    if (it.isEmpty()) red = "/oauth2/authorization/login-client";
                    return ServerResponse.temporaryRedirect(URI.create(red)).build();
                }));
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .anyExchange().permitAll()
                .and().oauth2Login()
                .and().formLogin().loginPage("/login")
                .and().logout()
                .logoutUrl("/signout").

                        logoutSuccessHandler((webFilterExchange, authentication) -> {
                            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
                            return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
                        })
                .and().csrf().csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                .requireCsrfProtectionMatcher(pathMatchers("/auth"))
                .and().build();
    }


    @Bean
    public GlobalFilter filter(ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        return (exchange, chain) -> exchange.getPrincipal()
                .cast(OAuth2AuthenticationToken.class)
                .flatMap(authentication -> authorizedClientRepository.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication, exchange).cast(OAuth2AuthorizedClient.class))
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(token -> exchange.mutate().request(r -> r.headers(headers -> headers.setBearerAuth(token.getTokenValue()))).build())
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter)
                .then(Mono.fromRunnable(() -> {
                    String name = "Set-Cookie";
                    String value = exchange.getResponse().getHeaders().getFirst(name);
                    if (!new PathPatternParser().parse("/auth/**").matches(exchange.getRequest().getPath().pathWithinApplication()) && value != null) {
                        exchange.getResponse().getHeaders().set(name, value.replaceAll("JSESSIONID=[0-9a-zA-Z]+; ", ""));
                        if (value.contains("SESSION=;")) exchange.getResponse().getHeaders().remove(name);
                    }
                }));
    }
}
