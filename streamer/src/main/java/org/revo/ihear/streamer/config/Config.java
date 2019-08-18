package org.revo.ihear.streamer.config;

import org.revo.ihear.streamer.codec.base.RtpPkt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;

import java.time.Duration;
import java.util.stream.Collectors;

@Configuration
public class Config {

    @Bean
    public FluxProcessor<RtpPkt, RtpPkt> processor() {
        return ReplayProcessor.create(0);

    }


    @Bean
    public Flux<RtpPkt> stream(FluxProcessor<RtpPkt, RtpPkt> processor) {
        return processor.publish().autoConnect()
                .buffer(Duration.ofMillis(350))
                .flatMap(it -> Flux.fromIterable(it.stream().sorted().collect(Collectors.toList())));
    }
}
