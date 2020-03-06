package org.revo.ihear.ui.util;

import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class Utils {
    public static Mono<CsrfToken> generateToken(CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository, ServerWebExchange exchange) {
        return cookieServerCsrfTokenRepository.loadToken(exchange)
                .switchIfEmpty(cookieServerCsrfTokenRepository.generateToken(exchange)
                        .delayUntil(token -> cookieServerCsrfTokenRepository.saveToken(exchange, token)))
                .doOnNext(it -> exchange.getAttributes().put(CsrfToken.class.getName(), it));
    }
}
