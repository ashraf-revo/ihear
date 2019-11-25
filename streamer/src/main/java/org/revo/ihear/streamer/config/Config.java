package org.revo.ihear.streamer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;

import java.time.Duration;

@Configuration
public class Config {
    @Bean
    public FluxProcessor<Message<byte[]>, Message<byte[]>> processor() {
//        return UnicastProcessor.create();
//        return ReplayProcessor.create();
//        return ReplayProcessor.create(0);
//        return DirectProcessor.create();
//        return TopicProcessor.create();
//        i need it no cache , i don,t need it to buffer any thing , i need it to be live stream
//        think it as rtp jitter buffer
        return ReplayProcessor.createTimeout(Duration.ofMillis(250));
    }


    @Bean
    public Flux<Message<byte[]>> stream(FluxProcessor<Message<byte[]>, Message<byte[]>> processor) {
        return processor.publish().autoConnect();
    }
}
