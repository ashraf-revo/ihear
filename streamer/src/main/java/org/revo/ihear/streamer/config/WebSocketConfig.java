package org.revo.ihear.streamer.config;

import org.revo.base.service.auth.AuthService;
import org.revo.base.service.stream.StreamService;
import org.revo.ihear.livepoll.rtsp.rtp.Encoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpAdtsFrameEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpNaluEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.base.AdtsFrame;
import org.revo.ihear.livepoll.rtsp.rtp.base.NALU;
import org.revo.ihear.livepoll.rtsp.rtp.base.RtpPkt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.publisher.Flux;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static reactor.core.publisher.Flux.fromIterable;

@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping handlerMapping(AuthService authService, StreamService streamService, Flux<Message<byte[]>> stream) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/streamer/audio/{id}", session -> {
            Encoder<RtpPkt, AdtsFrame> rtpAdtsFrameEncoder = new RtpAdtsFrameEncoder();
            String id = Paths.get(session.getHandshakeInfo().getUri().getPath()).getFileName().toString();
            Flux<WebSocketMessage> ss = stream
                    .filter(it -> "Audio".equals(it.getHeaders().get("type"))).filter(it -> Objects.equals(it.getHeaders().get("streamId"), id))
                    .filter(it -> it.getPayload().length > 0)
                    .map(Message::getPayload)
                    .map(it -> new RtpPkt(0, it))
                    .map(rtpAdtsFrameEncoder::encode)
                    .flatMap(Flux::fromIterable)
                    .map(AdtsFrame::getRaw)
                    .map(it -> session.binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(it)));
            return session.send(ss);
        });
        map.put("/streamer/video/{id}", session -> {
            Encoder<RtpPkt, NALU> rtpNaluEncoder = new RtpNaluEncoder();
            String id = Paths.get(session.getHandshakeInfo().getUri().getPath()).getFileName().toString();
            Flux<WebSocketMessage> ss = fromIterable(streamService.findOneById(id)
                    .map(it -> Stream.of(it.getSps(), it.getPps(), it.getIdr(), it.getSei()).filter(itb -> itb != null && itb.length > 0).map(NALU::getRaw).collect(Collectors.toList())).orElse(Collections.emptyList()))
                    .mergeWith(stream.filter(it -> "Video".equals(it.getHeaders().get("type"))).filter(it -> Objects.equals(it.getHeaders().get("streamId"), id)).filter(it -> it.getPayload().length > 0)
                            .map(Message::getPayload)
                            .map(it -> new RtpPkt(0, it))
                            .map(rtpNaluEncoder::encode)
                            .flatMap(Flux::fromIterable)
                            .map(NALU::getRaw)
                    )
                    .map(it -> session.binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(it)));
            return session.send(ss);
        });
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter(new HandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy()));
    }

}
