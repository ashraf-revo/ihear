package org.revo.ihear.pi.config;

import org.revo.base.service.auth.AuthService;
import org.revo.ihear.pi.config.domain.WSMessage;
import org.revo.ihear.pi.event.base.MessageReceivedEvent;
import org.revo.ihear.pi.event.base.SessionConnectEvent;
import org.revo.ihear.pi.event.base.SessionDisconnectEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.util.List;

import static org.springframework.messaging.support.MessageBuilder.withPayload;

public class WsSocketHandler implements WebSocketHandler {
    private AuthService authService;
    private ApplicationEventPublisher applicationEventPublisher;
    private Flux<Message<WSMessage>> messageFlux;

    WsSocketHandler(AuthService authService, ApplicationEventPublisher applicationEventPublisher, UnicastProcessor<Message<WSMessage>> wsMessages) {
        this.authService = authService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.messageFlux = wsMessages.publish().autoConnect();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        applicationEventPublisher.publishEvent(new SessionConnectEvent(this, session));
        Flux<Message<WSMessage>> in = session.receive()
                .flatMap(itm -> getToken(session).flatMap(itt -> authService.currentJwtUserId(itt)).map(it -> new WSMessage().setPayload(itm.getPayloadAsText()).setFrom(it)))
                .log("received")
                .flatMap(its -> getToken(session).flatMap(itt -> authService.currentJwtUserId(itt)).map(its::setFrom))
                .map(it -> withPayload(it).build())
                .doOnEach(it -> applicationEventPublisher.publishEvent(new MessageReceivedEvent(WsSocketHandler.this, it.get())))
                .doOnComplete(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(WsSocketHandler.this, session)))
                .doOnCancel(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(WsSocketHandler.this, session)));
        Flux<WebSocketMessage> out = messageFlux
                .flatMap(itm -> getToken(session).flatMap(it -> authService.currentJwt(it)).map(it -> itm))
                .log("send")
                .map(Message::getPayload)
                .filterWhen(it -> getToken(session).flatMap(itt -> authService.currentJwtUserId(itt)).filter(itw -> itw.equals(it.getTo())).map(itv -> true).defaultIfEmpty(it.getTo() == null))
                .map(WSMessage::getPayload).map(session::textMessage)
                .doOnCancel(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(WsSocketHandler.this, session)));
        return session.send(out).and(in);
    }


    private static Mono<String> getToken(WebSocketSession session) {
        List<String> authorization = session.getHandshakeInfo().getHeaders().get("Authorization");
        if (authorization == null) return Mono.empty();
        if (authorization.size() == 0) return Mono.empty();
        String bearer = authorization.get(0);
        if (bearer != null && !bearer.isEmpty() && !bearer.startsWith("Bearer ")) return Mono.empty();
        return Mono.just(bearer.substring(7));
    }
}
