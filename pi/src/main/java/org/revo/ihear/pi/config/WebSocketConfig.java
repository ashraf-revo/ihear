package org.revo.ihear.pi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.revo.ihear.pi.config.domain.WSMessage;
import org.revo.ihear.service.auth.AuthService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping handlerMapping(AuthService authService, ApplicationEventPublisher applicationEventPublisher, FluxProcessor<Message<WSMessage>, Message<WSMessage>> wsMessages) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        map.put("/echo/{user}/{device}", new WsSocketHandler(authService, applicationEventPublisher, wsMessages, mapper));
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
    public FluxProcessor<Message<WSMessage>, Message<WSMessage>> processor() {
        return ReplayProcessor.createTimeout(Duration.ofMillis(1));
    }

}
