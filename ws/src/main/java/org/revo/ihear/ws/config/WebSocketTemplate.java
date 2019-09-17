package org.revo.ihear.ws.config;

import org.revo.ihear.ws.config.domain.WSMessage;
import org.springframework.messaging.Message;

public interface WebSocketTemplate {
    void send(Message<WSMessage> wsMessage);
}
