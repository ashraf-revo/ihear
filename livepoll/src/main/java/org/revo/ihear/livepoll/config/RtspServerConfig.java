package org.revo.ihear.livepoll.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.rtsp.RtspEncoder;
import org.revo.base.domain.Stream;
import org.revo.base.service.auth.AuthService;
import org.revo.ihear.livepoll.config.rtspHandler.HolderImpl;
import org.revo.ihear.livepoll.config.rtspHandler.RtspMessageHandlerImpl;
import org.revo.ihear.livepoll.rtsp.codec.RtspRequestDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Configuration
public class RtspServerConfig implements ApplicationListener<ApplicationStartedEvent> {
    @Autowired
    private ServerBootstrap serverBootstrap;

    @Value("${security.check:false}")
    public boolean securitCheck;

    @Bean
    public NioEventLoopGroup group() {
        return new NioEventLoopGroup();
    }

    @Bean
    public ServerBootstrap serverBootstrap(NioEventLoopGroup group, HolderImpl holderImpl, @Value("${server.port:8085}") Integer port) {
        return build(group, holderImpl, port);
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        try {
            serverBootstrap.bind().sync();
        } catch (Exception ignored) {
        }
    }

    private static ServerBootstrap build(NioEventLoopGroup group, HolderImpl holderImpl, Integer port) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(group);
        serverBootstrap.localAddress(port);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 0);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_RCVBUF, 128 * 1500)
                .childOption(ChannelOption.SO_SNDBUF, 256 * 1500)
                .childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(new RtspEncoder()).addLast(new RtspRequestDecoder()).addLast(new RtspMessageHandlerImpl(holderImpl));
            }
        });
        return serverBootstrap;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri") String jwkSetUri) {
        return new NimbusReactiveJwtDecoder(jwkSetUri);
    }

    @Bean
    public Function<DefaultFullHttpRequest, Mono<Stream>> authorizationCheck(AuthService authService) {
        if (!securitCheck) return req -> Mono.just(new Stream());
        return req -> Mono.just(URI.create(req.uri()).getPath().split("/"))
                .filter(it -> it.length >= 4).flatMap(parts -> authService.remoteStream(parts[2], parts[3]));
    }
}
