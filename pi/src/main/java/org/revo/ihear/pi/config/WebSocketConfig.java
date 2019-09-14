package org.revo.ihear.pi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Autowired
    private ReactiveJwtDecoder reactiveJwtDecoder;


    private Mono<Jwt> getJwt(WebSocketSession session) {
        List<String> authorization = session.getHandshakeInfo().getHeaders().get("Authorization");
        if (authorization == null) return Mono.empty();
        if (authorization.size() == 0) return Mono.empty();
        String bearer = authorization.get(0);
        if (bearer != null && !bearer.isEmpty() && !bearer.startsWith("Bearer ")) return Mono.empty();
        return reactiveJwtDecoder.decode(bearer.substring(7));
    }

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws", session ->
                session.send(session.receive()
                        .filterWhen(it -> getJwt(session).map(itv -> true).defaultIfEmpty(false))
                        .map(value -> session.textMessage(value.getPayloadAsText())))

        );

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter(new HandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy()));
    }
}
