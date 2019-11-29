package org.revo.ihear.livepoll.config.rtspHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.rtsp.RtspMethods;
import org.revo.ihear.livepoll.rtsp.RtspSession;
import org.revo.ihear.livepoll.rtsp.action.*;
import org.revo.ihear.livepoll.rtsp.d.InterLeavedRTPSession;
import org.revo.ihear.livepoll.rtsp.rtp.base.RtpPkt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class RtspMessageHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(RtspMessageHandler.class);

    abstract void handleRtsp(ChannelHandlerContext ctx, DefaultFullHttpRequest request);

    abstract void handleRtp(InterLeavedRTPSession rtpSession, RtpPkt rtpPkt);

    abstract void setState(HttpMethod state);

    abstract HttpMethod getState();

    abstract RtspSession getSession();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DefaultFullHttpRequest) {
            logger.info(msg.toString());
            DefaultFullHttpRequest request = (DefaultFullHttpRequest) msg;
            if (request.method() == RtspMethods.OPTIONS) {
                ctx.writeAndFlush(new OptionsAction(request, getSession()).call());
                setState(RtspMethods.OPTIONS);
            } else if (getState() == RtspMethods.OPTIONS && request.method() == RtspMethods.ANNOUNCE) {
                handleRtsp(ctx, request);
                ctx.writeAndFlush(new AnnounceAction(request, getSession()).call());
                setState(RtspMethods.ANNOUNCE);
            } else if ((getState() == RtspMethods.ANNOUNCE || getState() == RtspMethods.SETUP) && request.method() == RtspMethods.SETUP) {
                ctx.writeAndFlush(new SetupAction(request, getSession()).call());
                setState(RtspMethods.SETUP);
            } else if (getState() == RtspMethods.SETUP && request.method() == RtspMethods.RECORD) {
                ctx.writeAndFlush(new RecordAction(request, getSession()).call());
                setState(RtspMethods.RECORD);
            } else if (request.method() == RtspMethods.TEARDOWN) {
                ctx.writeAndFlush(new TeardownAction(request, getSession()).call());
                setState(RtspMethods.TEARDOWN);
            } else close(ctx, "not follwing rtsp seqance (OPTIONS,ANNOUNCE,SETUP,RECORD,TEARDOWN)");
        } else if (msg instanceof RtpPkt) {
            if (getState() != RtspMethods.RECORD)
                close(ctx, "not follwing rtsp seqance (OPTIONS,ANNOUNCE,SETUP,RECORD,TEARDOWN)");
            RtpPkt rtpPkt = (RtpPkt) msg;
            InterLeavedRTPSession rtpSession = getSession().getRTPSessions()[getSession().getStreamIndex(rtpPkt.getRtpChannle())];
            if (rtpPkt.getRtpChannle() == rtpSession.rtpChannel()) {
                handleRtp(rtpSession, rtpPkt);
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
    }

    public void close(ChannelHandlerContext ctx, String reasoon) {
        try {
            logger.info("will close because " + reasoon);
            ctx.close().sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    public Logger getLogger() {
        return logger;
    }
}


