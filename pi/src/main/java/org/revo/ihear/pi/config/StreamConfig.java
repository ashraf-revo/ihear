package org.revo.ihear.pi.config;

import org.revo.ihear.pi.config.domain.WSMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import reactor.core.publisher.UnicastProcessor;

@Configuration
@EnableBinding(Processor.class)
@Profile("!direct")
public class StreamConfig {
    @Autowired
    private UnicastProcessor<Message<WSMessage>> wsMessages;

    @StreamListener(Sink.INPUT)
    public void handle(Message<WSMessage> message) {
        this.wsMessages.onNext(message);
    }
}
