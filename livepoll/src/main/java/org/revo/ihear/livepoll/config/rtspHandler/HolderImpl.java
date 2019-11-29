package org.revo.ihear.livepoll.config.rtspHandler;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import org.revo.base.domain.Stream;
import org.revo.ihear.livepoll.config.PiSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public class HolderImpl {
    @Autowired
    private PiSource piSource;
    @Autowired
    private Function<DefaultFullHttpRequest, Mono<Stream>> authorizationCheck;

    public PiSource getPiSource() {
        return piSource;
    }

    public Function<DefaultFullHttpRequest, Mono<Stream>> getAuthorizationCheck() {
        return authorizationCheck;
    }
}
