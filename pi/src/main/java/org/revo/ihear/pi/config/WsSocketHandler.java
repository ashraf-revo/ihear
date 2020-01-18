package org.revo.ihear.pi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.revo.ihear.pi.config.domain.WSMessage;
import org.revo.ihear.pi.event.base.SessionConnectEvent;
import org.revo.ihear.pi.event.base.SessionDisconnectEvent;
import org.revo.ihear.service.auth.AuthService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class WsSocketHandler implements WebSocketHandler {
    private AuthService authService;
    private ApplicationEventPublisher applicationEventPublisher;
    private Flux<Message<WSMessage>> messageFlux;
    private ObjectMapper mapper;

    WsSocketHandler(AuthService authService, ApplicationEventPublisher applicationEventPublisher, FluxProcessor<Message<WSMessage>, Message<WSMessage>> wsMessages, ObjectMapper mapper) {
        this.authService = authService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.messageFlux = wsMessages.publish().autoConnect();
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        applicationEventPublisher.publishEvent(new SessionConnectEvent(this, session));
        Flux<WebSocketMessage> out = getToken(session).flatMap(itt -> authService.currentJwtUserId(itt)).
                map(its -> session.getHandshakeInfo().getUri().getPath().replace("user", its))
                .flatMapMany(sub -> messageFlux.map(Message::getPayload)
                        .log("send")
                        .filter(it -> it.getPayload() != null && sub.equals(it.getTo()))
                        .map(WSMessage -> session.textMessage(convert(WSMessage.getPayload())))
                )
                .doOnCancel(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(WsSocketHandler.this, session)));


//        Optional<String> urlCheck1 = getToken(session).flatMap(itt -> authService.currentJwtUserId(itt)).
//                map(its -> session.getHandshakeInfo().getUri().getPath().replace("sub", its)).blockOptional();
//        System.out.println(urlCheck1.get());


        //        Flux<Message<WSMessage>> in = session.receive()
//                .flatMap(itm -> getToken(session).flatMap(itt -> authService.currentJwtUserId(itt)).map(it -> new WSMessage().setPayload(itm.getPayloadAsText()).setFrom(it)))
//                .log("received")
//                .flatMap(its -> getToken(session).flatMap(itt -> authService.currentJwtUserId(itt)).map(its::setFrom))
//                .map(it -> withPayload(it).build())
//                .doOnEach(it -> applicationEventPublisher.publishEvent(new MessageReceivedEvent(WsSocketHandler.this, it.get())))
//                .doOnComplete(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(WsSocketHandler.this, session)))
//                .doOnCancel(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(WsSocketHandler.this, session)));

//        Flux<WebSocketMessage> out = messageFlux
//                .flatMap(itm -> getToken(session).flatMap(it -> authService.currentJwt(it)).map(it -> itm))
//                .log("send")
//                .map(Message::getPayload)
//                .filter(it -> it.getPayload() != null)
//                .filterWhen(it -> urlCheck.filter(itUrl -> itUrl.equals(it.getTo())).map(itas -> true).defaultIfEmpty(false))
//                .map(WSMessage::getPayload).map(convertToJson).map(session::textMessage)
//                .doOnCancel(() -> applicationEventPublisher.publishEvent(new SessionDisconnectEvent(WsSocketHandler.this, session)));
        return session.send(out)
//                .and(in)
                ;
    }


    private static Mono<String> getToken(WebSocketSession session) {
        List<String> authorization = session.getHandshakeInfo().getHeaders().get("Authorization");
        if (authorization == null) return Mono.empty();
        if (authorization.size() == 0) return Mono.empty();
        String bearer = authorization.get(0);
        if (bearer != null && !bearer.isEmpty() && !bearer.startsWith("Bearer ")) return Mono.empty();
        return Mono.just(bearer.substring(7));
    }

    private String convert(Map<String, Object> payload) {
        try {
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            return "{}";
        }
    }
}
