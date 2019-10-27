package org.revo.ihear.pi.config;

import org.revo.ihear.pi.config.domain.WSMessage;
import org.springframework.messaging.Message;

public interface WebSocketTemplate {
    void send(Message<WSMessage> wsMessage);
}
