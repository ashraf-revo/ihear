package org.revo.ihear.livepoll.config.rtspHandler;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import org.revo.base.service.stream.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.function.Function;

@Component
public class HolderImpl {
    @Autowired
    private Source source;
    @Autowired
    private StreamService streamService;
    @Autowired
    private Function<DefaultFullHttpRequest, Mono<LinkedHashMap>> authorizationCheck;

    public Source getSource() {
        return source;
    }

    public StreamService getStreamService() {
        return streamService;
    }

    public Function<DefaultFullHttpRequest, Mono<LinkedHashMap>> getAuthorizationCheck() {
        return authorizationCheck;
    }
}
