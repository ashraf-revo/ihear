package org.revo.ihear.livepoll.rtsp.action;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import org.revo.ihear.livepoll.rtsp.RtspSession;

import java.util.concurrent.Callable;

abstract class BaseAction<T extends DefaultFullHttpRequest> implements Callable<DefaultFullHttpResponse> {
    T req;
    RtspSession rtspSession;

    BaseAction(T req, RtspSession rtspSession) {
        this.req = req;
        this.rtspSession = rtspSession;
    }
}
