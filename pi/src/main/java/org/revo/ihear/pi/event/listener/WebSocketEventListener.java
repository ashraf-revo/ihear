package org.revo.ihear.pi.event.listener;

import org.revo.ihear.pi.config.WebSocketSessionRegistry;
import org.revo.ihear.pi.config.WebSocketTemplate;
import org.revo.ihear.pi.event.base.MessageReceivedEvent;
import org.revo.ihear.pi.event.base.SessionConnectEvent;
import org.revo.ihear.pi.event.base.SessionDisconnectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class WebSocketEventListener {
    @Autowired
    private WebSocketSessionRegistry webSocketSessionRegistry;
    @Autowired
    private ReactiveJwtDecoder reactiveJwtDecoder;
    @Autowired
    private WebSocketTemplate webSocketTemplate;

    @EventListener
    public void sessionDisconnectEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        System.out.println("in SessionDisconnectEvent " + sessionDisconnectEvent.getSession().getId());
        webSocketSessionRegistry.remove(sessionDisconnectEvent.getSession().getId());


        System.out.println("****************** ********************** ************* active after disconnect");
        for (String allActiveUser : webSocketSessionRegistry.getAllActiveUsers()) {
            System.out.println("user " + allActiveUser);
        }
    }

    @EventListener
    public void sessionConnectEvent(SessionConnectEvent sessionConnectEvent) {
        System.out.println("in SessionConnectEvent " + sessionConnectEvent.getSession().getId());
        getUserId(sessionConnectEvent.getSession()).subscribe(it -> webSocketSessionRegistry.add(it, sessionConnectEvent.getSession().getId()), System.out::println);

        System.out.println("****************** ********************** ************* active after connect");
        for (String allActiveUser : webSocketSessionRegistry.getAllActiveUsers()) {
            System.out.println("user " + allActiveUser);
        }

    }

    @EventListener
    public void messageReceivedEvent(MessageReceivedEvent messageReceivedEvent) {
        System.out.println("in MessageReceivedEvent " + messageReceivedEvent.getWsMessage().getPayload());
        webSocketTemplate.send(messageReceivedEvent.getWsMessage());
    }

    private Mono<String> getUserId(WebSocketSession session) {
        List<String> authorization = session.getHandshakeInfo().getHeaders().get("Authorization");
        if (authorization == null) return Mono.empty();
        if (authorization.size() == 0) return Mono.empty();
        String bearer = authorization.get(0);
        if (bearer != null && !bearer.isEmpty() && !bearer.startsWith("Bearer ")) return Mono.empty();
        return reactiveJwtDecoder.decode(bearer.substring(7))
                .map(Jwt::getSubject);
    }

}
