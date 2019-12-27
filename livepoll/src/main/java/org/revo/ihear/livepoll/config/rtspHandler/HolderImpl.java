package org.revo.ihear.livepoll.config.rtspHandler;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import org.revo.base.domain.Stream;
import org.revo.base.service.auth.AuthService;
import org.revo.ihear.livepoll.config.PiSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Component
public class HolderImpl {
    @Autowired
    private PiSource piSource;
    @Autowired
    private AuthService authService;
    @Value("${security.check:false}")
    private boolean securitCheck;

    public PiSource getPiSource() {
        return piSource;
    }

    public Function<DefaultFullHttpRequest, Mono<Stream>> getAuthorizationCheck() {
        if (!securitCheck) return req -> Mono.just(new Stream());
        return req -> Mono.just(URI.create(req.uri()).getPath().split("/"))
                .filter(it -> it.length >= 4).flatMap(parts -> authService.remoteStream(parts[2], parts[3]));
    }
}
