package org.revo.ihear.streamer.config;

import org.revo.ihear.livepoll.rtsp.rtp.Encoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpNaluEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.base.NALU;
import org.revo.ihear.livepoll.rtsp.rtp.base.RtpPkt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;

@Configuration
public class Config {
    private final Encoder<RtpPkt, NALU> rtpPktToNalu = new RtpNaluEncoder();

    @Bean
    public FluxProcessor<RtpPkt, RtpPkt> processor() {
        return ReplayProcessor.create(0);

    }


    @Bean
    public Flux<NALU> stream(FluxProcessor<RtpPkt, RtpPkt> processor) {
        return processor.publish().autoConnect().
                map(rtpPktToNalu::encode)
                .flatMap(Flux::fromIterable)

//                .buffer(Duration.ofMillis(350))
//                .flatMap(it -> Flux.fromIterable(it.stream().sorted().collect(Collectors.toList())))
                ;
    }
}
