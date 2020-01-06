package org.revo.ihear.livepoll.config;

import io.netty.handler.codec.rtsp.RtspEncoder;
import org.revo.ihear.livepoll.config.rtspHandler.HolderImpl;
import org.revo.ihear.livepoll.config.rtspHandler.RtspRtpHandler;
import org.revo.ihear.livepoll.rtsp.codec.RtspRequestDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

@Configuration
public class RtspServerConfig {
    @Bean
    public DisposableServer tcpServer(HolderImpl holder, @Value("${server.port:8085}") Integer port) {
        return TcpServer.create().port(port + 1)
                .doOnConnection(it -> it.addHandlerLast(new RtspEncoder()).addHandlerLast(new RtspRequestDecoder()))
                .handle((inbound, outbound) -> outbound.sendObject(inbound.receiveObject().flatMap(new RtspRtpHandler(holder)))).bindNow();
    }
}
