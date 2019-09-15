package org.revo.ihear.ws.config;

import org.revo.ihear.ws.event.base.MessageReceivedEvent;
import org.revo.ihear.ws.event.base.SessionConnectEvent;
import org.revo.ihear.ws.event.base.SessionDisconnectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


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
        map.put("/ws", session -> {
                    applicationEventPublisher.publishEvent(new SessionConnectEvent(this, session));
                    return session.send(session.receive()
                            .filterWhen(it -> getJwt(session).map(itv -> true).defaultIfEmpty(false))
                            .map(value -> session.textMessage(value.getPayloadAsText()))
                            .doOnComplete(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(this, session)))
                            .doOnEach(webSocketMessageSignal -> {
                                if (webSocketMessageSignal.get() != null)
                                    applicationEventPublisher.publishEvent(new MessageReceivedEvent(this, session, webSocketMessageSignal.get()));
                            }));
                }
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

    @Bean
    public WebSocketSessionRegistry webSocketSessionRegistry() {
        return new WebSocketSessionRegistry();
    }
}
