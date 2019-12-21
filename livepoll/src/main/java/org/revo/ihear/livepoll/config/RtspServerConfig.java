package org.revo.ihear.livepoll.config;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.rtsp.RtspEncoder;
import org.revo.base.domain.Stream;
import org.revo.base.service.auth.AuthService;
import org.revo.ihear.livepoll.config.rtspHandler.HolderImpl;
import org.revo.ihear.livepoll.config.rtspHandler.RtspRtpHandler;
import org.revo.ihear.livepoll.rtsp.codec.RtspRequestDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.net.URI;
import java.util.function.Function;

@Configuration
public class RtspServerConfig {

    @Bean
    public DisposableServer tcpServer(HolderImpl holder, @Value("${server.port:8085}") Integer port) {
        return TcpServer.create().port(port)
                .doOnConnection(it -> it.addHandlerLast(new RtspEncoder()).addHandlerLast(new RtspRequestDecoder()))
                .handle((inbound, outbound) -> outbound.sendObject(inbound.receiveObject().flatMap(new RtspRtpHandler(holder)))).bindNow();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri") String jwkSetUri) {
        return new NimbusReactiveJwtDecoder(jwkSetUri);
    }

    @Bean
    public Function<DefaultFullHttpRequest, Mono<Stream>> authorizationCheck(AuthService authService, @Value("${security.check:false}") boolean securitCheck) {
        if (!securitCheck) return req -> Mono.just(new Stream());
        return req -> Mono.just(URI.create(req.uri()).getPath().split("/"))
                .filter(it -> it.length >= 4).flatMap(parts -> authService.remoteStream(parts[2], parts[3]));
    }
}
