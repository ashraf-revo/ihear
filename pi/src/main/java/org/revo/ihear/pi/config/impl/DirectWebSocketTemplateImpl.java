package org.revo.ihear.pi.config.impl;

import org.revo.ihear.pi.config.WebSocketTemplate;
import org.revo.ihear.pi.config.domain.WSMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.UnicastProcessor;

@Service
@Profile("direct")
public class DirectWebSocketTemplateImpl implements WebSocketTemplate {
    @Autowired
    private UnicastProcessor<Message<WSMessage>> wsMessages;

    @Override
    public void send(Message<WSMessage> wsMessage) {
        this.wsMessages.onNext(wsMessage);
    }
}
