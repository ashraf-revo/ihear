package org.revo.ihear.ws.config.Impl;

import org.revo.ihear.ws.config.WebSocketTemplate;
import org.revo.ihear.ws.config.domain.WSMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.UnicastProcessor;

@Service
public class WebSocketTemplateImpl implements WebSocketTemplate {
    @Autowired
    private Processor processor;
    @Autowired
    private UnicastProcessor<Message<WSMessage>> wsMessages;

    @Override
    public void send(Message<WSMessage> wsMessage) {
        this.processor.output().send(wsMessage);
//        this.wsMessages.onNext(wsMessage);
    }

    @StreamListener(Sink.INPUT)
    public void handle(Message<WSMessage> message) {
        this.wsMessages.onNext(message);
    }
}
