package org.revo.ihear.ui.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.security.web.server.util.matcher.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class ServerRequestCacheImpl extends WebSessionServerRequestCache {
    private ServerWebExchangeMatcher saveRequestMatcher = createDefaultRequestMacher();
    private static final String DEFAULT_SAVED_REQUEST_ATTR = "SPRING_SECURITY_SAVED_REQUEST";
    private String sessionAttrName = DEFAULT_SAVED_REQUEST_ATTR;

    private ServerWebExchangeMatcher createDefaultRequestMacher() {
        ServerWebExchangeMatcher get = ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/**");
        ServerWebExchangeMatcher notFavicon = new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers("/favicon.*"));
        MediaTypeServerWebExchangeMatcher html = new MediaTypeServerWebExchangeMatcher(MediaType.TEXT_HTML);
        html.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        return new AndServerWebExchangeMatcher(get, notFavicon, html);
    }

    @Override
    public Mono<Void> saveRequest(ServerWebExchange exchange) {
        return this.saveRequestMatcher.matches(exchange)
                .filter(m -> m.isMatch())
                .flatMap(m -> exchange.getSession())
                .map(WebSession::getAttributes)
                .doOnNext(attrs -> attrs.put(this.sessionAttrName, exchange.getRequest().getQueryParams().get("lastRoute").get(0)))
                .then();
    }

}
