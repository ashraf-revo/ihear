package org.revo.ihear.ui.controller;

import org.revo.ihear.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.util.function.Tuple2;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.revo.ihear.ui.util.Utils.generateToken;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RestController
public class MainController {
    private static final List<String> services = Arrays.asList("/auth/**", "/pi/**", "/echo/**", "/login");
    private final RequestPredicate requestPredicate = serverRequest -> services.stream().map(it -> new PathPatternParser().parse(it))
            .noneMatch(it -> it.matches(serverRequest.exchange().getRequest().getPath().pathWithinApplication())) && !serverRequest.path().contains(".");

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
}
