package org.revo.ihear.ws.config;

import org.revo.ihear.ws.event.base.MessageReceivedEvent;
import org.revo.ihear.ws.event.base.SessionConnectEvent;
import org.revo.ihear.ws.event.base.SessionDisconnectEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.List;

public class WsSocketHandler implements WebSocketHandler {
    private ReactiveJwtDecoder reactiveJwtDecoder;
    private ApplicationEventPublisher applicationEventPublisher;

    public WsSocketHandler(ReactiveJwtDecoder reactiveJwtDecoder, ApplicationEventPublisher applicationEventPublisher) {
        this.reactiveJwtDecoder = reactiveJwtDecoder;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
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

    private Mono<Jwt> getJwt(WebSocketSession session) {
        List<String> authorization = session.getHandshakeInfo().getHeaders().get("Authorization");
        if (authorization == null) return Mono.empty();
        if (authorization.size() == 0) return Mono.empty();
        String bearer = authorization.get(0);
        if (bearer != null && !bearer.isEmpty() && !bearer.startsWith("Bearer ")) return Mono.empty();
        return reactiveJwtDecoder.decode(bearer.substring(7));
    }

}
