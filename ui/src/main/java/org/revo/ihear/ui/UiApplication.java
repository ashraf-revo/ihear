package org.revo.ihear.ui;

import org.revo.base.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;

import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWebFluxSecurity
@ComponentScan(basePackages = {"org.revo.base.service.auth", "org.revo.ihear.ui"})
@EnableFeignClients
public class UiApplication {
    private static final List<String> services = Arrays.asList("/auth/**", "/pi/**", "/streamer/**", "/echo/**", "/login", "/loginx", "/user");
    private final RequestPredicate requestPredicate = serverRequest -> services.stream().map(it -> new PathPatternParser().parse(it))
            .noneMatch(it -> it.matches(serverRequest.exchange().getRequest().getPath().pathWithinApplication())) && !serverRequest.path().contains(".");
    @Autowired
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> routes(@Value("classpath:/static/index.html") Resource index, AuthService authService, OAuth2ClientProperties oAuth2ClientProperties) {
        return route(requestPredicate, serverRequest -> ok().contentType(MediaType.TEXT_HTML).syncBody(index))
                .andRoute(GET("/login"), serverRequest -> authService.currentOAuth2User().defaultIfEmpty("").flatMap(it -> {
                    String red = "/";
                    if (it.isEmpty())
                        red = "/oauth2/authorization/" + serverRequest.queryParam("app").orElseGet(() -> oAuth2ClientProperties.getRegistration().entrySet().stream().findAny().map(Map.Entry::getKey).get());
                    return ServerResponse.temporaryRedirect(URI.create(red)).build();
                }))
                .andRoute(RequestPredicates.POST("/loginx"),
                        serverRequest -> {
                            Mono<Void> voidMono = serverRequest.bodyToMono(HashMap.class).flatMap(it -> authService.currentJwt(it.get("access_token").toString())
                                    .flatMap(jwt -> {
                                        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt());
                                        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(it.get("refresh_token").toString(), jwt.getIssuedAt());

                                        ClientRegistration device = ClientRegistration.withRegistrationId("device").authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).tokenUri("http://localhost:8080").clientId("device").build();

                                        DefaultOAuth2User principal = new DefaultOAuth2User(AuthorityUtils.createAuthorityList("read"), jwt.getClaims(), "sub");
                                        OAuth2AuthenticationToken oAuth2AuthenticationToken = new OAuth2AuthenticationToken(principal, new ArrayList<>(), "device");
                                        OAuth2AuthorizedClient oAuth2AuthorizedClient = new OAuth2AuthorizedClient(device, jwt.getSubject(), oAuth2AccessToken, refreshToken);
                                        return serverRequest.session()
                                                .doOnNext((session) -> session.getAttributes().put(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, new SecurityContextImpl(oAuth2AuthenticationToken)))
                                                .flatMap(webSession ->
                                                        authorizedClientRepository.saveAuthorizedClient(oAuth2AuthorizedClient, oAuth2AuthenticationToken, serverRequest.exchange()));
                                    }));
                            return ok().body(voidMono, Void.class);
                        })
                .andRoute(GET("/user"), serverRequest -> ok().body(authService.currentAuthentication().cast(OAuth2AuthenticationToken.class)
                        .flatMap(it -> authorizedClientRepository.loadAuthorizedClient(it.getAuthorizedClientRegistrationId(), it, serverRequest.exchange())), OAuth2AuthorizedClient.class));
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .pathMatchers("/desktop.html").authenticated()
                .pathMatchers("/android.html").authenticated()
                .anyExchange().permitAll()
                .and().oauth2Login()
                .and().formLogin().loginPage("/login")
                .and().logout()
                .logoutUrl("/signout").logoutSuccessHandler((webFilterExchange, authentication) -> {
                    webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
                    return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
                })
                .and().csrf().csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                .requireCsrfProtectionMatcher(pathMatchers("/auth"))
                .and().build();
    }

}
