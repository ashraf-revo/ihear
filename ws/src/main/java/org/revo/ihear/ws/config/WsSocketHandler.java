package org.revo.ihear.ws.config;

import org.revo.ihear.ws.config.domain.WSMessage;
import org.revo.ihear.ws.event.base.MessageReceivedEvent;
import org.revo.ihear.ws.event.base.SessionConnectEvent;
import org.revo.ihear.ws.event.base.SessionDisconnectEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.messaging.support.MessageBuilder.withPayload;

public class WsSocketHandler implements WebSocketHandler {
    private ReactiveJwtDecoder reactiveJwtDecoder;
    private ApplicationEventPublisher applicationEventPublisher;
    private Flux<Message<WSMessage>> messageFlux;

    public WsSocketHandler(ReactiveJwtDecoder reactiveJwtDecoder, ApplicationEventPublisher applicationEventPublisher, UnicastProcessor<Message<WSMessage>> wsMessages) {
        this.reactiveJwtDecoder = reactiveJwtDecoder;
        this.applicationEventPublisher = applicationEventPublisher;
        this.messageFlux = wsMessages.publish().autoConnect();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        applicationEventPublisher.publishEvent(new SessionConnectEvent(this, session));
        Flux<Message<WSMessage>> in = session.receive()
                .flatMap(itm -> getUserId(session).map(it -> new WSMessage().setPayload(itm.getPayloadAsText()).setFrom(it)))
                .log("received")
                .flatMap(its -> getUserId(session).map(its::setFrom))
                .map(it -> withPayload(it).build())
                .doOnEach(it -> applicationEventPublisher.publishEvent(new MessageReceivedEvent(this, it.get())))
                .doOnComplete(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(this, session)))
//                .doOnCancel(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(this, session)))
                ;
        Flux<WebSocketMessage> out = messageFlux
                .flatMap(itm -> getJwt(session).map(it -> itm))
                .log("send")
                .map(Message::getPayload)
                .filterWhen(it -> getUserId(session).filter(itw -> itw.equals(it.getTo())).map(itv -> true).defaultIfEmpty(it.getTo() == null))
                .map(WSMessage::getPayload).map(session::textMessage)
//                .doOnCancel(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(this, session)))
                ;
        return session.send(out).and(in);
    }

    private Mono<Jwt> getJwt(WebSocketSession session) {
        List<String> authorization = session.getHandshakeInfo().getHeaders().get("Authorization");
        if (authorization == null) return Mono.empty();
        if (authorization.size() == 0) return Mono.empty();
        String bearer = authorization.get(0);
        if (bearer != null && !bearer.isEmpty() && !bearer.startsWith("Bearer ")) return Mono.empty();
        return reactiveJwtDecoder.decode(bearer.substring(7));
    }

    private Mono<String> getUserId(WebSocketSession session) {
        return getJwt(session)
                .map(Jwt::getClaims)
                .map(it -> it.get("user"))
                .map(it -> (HashMap<String, String>) it)
                .flatMapMany(it -> Flux.fromStream(it.entrySet().stream()))
                .filter(it -> it.getKey().equals("id"))
                .map(Map.Entry::getValue)
                .next();
    }
}
