package org.revo.ihear.streamer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;

@Configuration
public class Config {
    @Bean
    public FluxProcessor<Message<byte[]>, Message<byte[]>> processor() {
        return ReplayProcessor.create(0);

    }


    @Bean
    public Flux<Message<byte[]>> stream(FluxProcessor<Message<byte[]>, Message<byte[]>> processor) {
        return processor.publish().autoConnect();
    }
}
