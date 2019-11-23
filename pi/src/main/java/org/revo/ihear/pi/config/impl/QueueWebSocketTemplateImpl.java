package org.revo.ihear.pi.config.impl;

import org.revo.ihear.pi.config.WebSocketTemplate;
import org.revo.ihear.pi.config.domain.WSMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@Profile("!direct")
public class QueueWebSocketTemplateImpl implements WebSocketTemplate {
    @Autowired
    private Processor processor;

    @Override
    public void send(Message<WSMessage> wsMessage) {
        this.processor.output().send(wsMessage);
    }
}
