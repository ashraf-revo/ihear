package org.revo.ihear.ws.event.listener;

import org.revo.ihear.ws.config.WebSocketSessionRegistry;
import org.revo.ihear.ws.event.base.MessageReceivedEvent;
import org.revo.ihear.ws.event.base.SessionConnectEvent;
import org.revo.ihear.ws.event.base.SessionDisconnectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketEventListener {
    @Autowired
    private WebSocketSessionRegistry webSocketSessionRegistry;
    @Autowired
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @EventListener
    public void sessionDisconnectEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        System.out.println("in SessionDisconnectEvent " + sessionDisconnectEvent.getSession().getId());
        getUserId(sessionDisconnectEvent.getSession()).subscribe(it -> webSocketSessionRegistry.remove(it, sessionDisconnectEvent.getSession()));

    }

    @EventListener
    public void sessionConnectEvent(SessionConnectEvent sessionConnectEvent) {
        System.out.println("in SessionConnectEvent " + sessionConnectEvent.getSession().getId());
        getUserId(sessionConnectEvent.getSession()).subscribe(it -> webSocketSessionRegistry.add(it, sessionConnectEvent.getSession()));
    }

    @EventListener
    public void messageReceivedEvent(MessageReceivedEvent messageReceivedEvent) {
        System.out.println("in MessageReceivedEvent " + messageReceivedEvent.getWebSocketMessage().getType() + "         " + messageReceivedEvent.getWebSocketMessage().getPayloadAsText());
    }

    private Mono<String> getUserId(WebSocketSession session) {
        List<String> authorization = session.getHandshakeInfo().getHeaders().get("Authorization");
        if (authorization == null) return Mono.empty();
        if (authorization.size() == 0) return Mono.empty();
        String bearer = authorization.get(0);
        if (bearer != null && !bearer.isEmpty() && !bearer.startsWith("Bearer ")) return Mono.empty();
        return reactiveJwtDecoder.decode(bearer.substring(7))
                .map(Jwt::getClaims)
                .map(it -> it.get("user"))
                .map(it -> (HashMap<String, String>) it)
                .flatMapMany(it -> Flux.fromStream(it.entrySet().stream()))
                .filter(it -> it.getKey().equals("id"))
                .map(Map.Entry::getValue)
                .next();
    }

}
