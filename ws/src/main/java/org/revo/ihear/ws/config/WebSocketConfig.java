package org.revo.ihear.ws.config;

import org.revo.ihear.ws.config.domain.WSMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.publisher.UnicastProcessor;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping handlerMapping(ReactiveJwtDecoder reactiveJwtDecoder, ApplicationEventPublisher applicationEventPublisher, UnicastProcessor<Message<WSMessage>> wsMessages) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws", new WsSocketHandler(reactiveJwtDecoder, applicationEventPublisher, wsMessages));
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

    @Bean
    public UnicastProcessor<Message<WSMessage>> processor() {
        return UnicastProcessor.create();
    }

}
