package org.revo.ihear.livepoll.config.rtspHandler;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import org.revo.base.service.stream.StreamService;
import org.revo.ihear.livepoll.service.UiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class HolderImpl {
    @Autowired
    private Source source;
    @Autowired
    private StreamService streamService;
    @Autowired
    private UiService uiService;
    @Autowired
    private Predicate<DefaultFullHttpRequest> authorizationCheck;

    public Source getSource() {
        return source;
    }

    public StreamService getStreamService() {
        return streamService;
    }

    public UiService getUiService() {
        return uiService;
    }

    public Predicate<DefaultFullHttpRequest> getAuthorizationCheck() {
        return authorizationCheck;
    }
}
