package org.revo.ihear.livepoll.config.rtspHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.rtsp.RtspMethods;
import org.revo.ihear.livepoll.rtsp.RtspSession;
import org.revo.ihear.livepoll.rtsp.action.*;
import org.revo.ihear.livepoll.rtsp.d.InterLeavedRTPSession;
import org.revo.ihear.livepoll.rtsp.d.MediaType;
import org.revo.ihear.livepoll.rtsp.rtp.base.RtpPkt;
import org.revo.ihear.livepoll.rtsp.utils.URLObject;
import org.springframework.messaging.support.MessageBuilder;

import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static java.util.Base64.getDecoder;


public class RtspMessageHandler extends ChannelInboundHandlerAdapter {
    private HolderImpl holderImpl;
    private RtspSession session;
    private String id;

    public RtspMessageHandler(HolderImpl holderImpl) {
        this.holderImpl = holderImpl;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        if (msg instanceof DefaultFullHttpRequest) {
            DefaultFullHttpRequest request = (DefaultFullHttpRequest) msg;
            System.out.println(request);
            if (request.method() == RtspMethods.OPTIONS) {
                ctx.writeAndFlush(new OptionsAction(request, this.session).call());
            }
            if (request.method() == RtspMethods.ANNOUNCE) {
                String[] sprop = new String[0];
                String sessionId = UUID.randomUUID().toString();
                this.session = new RtspSession(request.uri()).setId(sessionId).withSdp(request.content().toString(StandardCharsets.UTF_8));
                try {
                    for (Object mediaDescription : session.getSessionDescription().getMediaDescriptions(true)) {
                        MediaDescription description = (MediaDescription) mediaDescription;
                        if (description.getMedia().getMediaType().equals("video")) {
                            sprop = description.getAttribute("fmtp").split("; ")[1].replace("sprop-parameter-sets=", "").split(",");
                        }
                    }
                } catch (SdpException e) {
                    ctx.close().sync();
                }
                this.id = URLObject.getId(request.uri());
                if (sprop.length == 2 && this.holderImpl.getStreamService().setSpsPps(id, sessionId, getDecoder().decode(sprop[0]), getDecoder().decode(sprop[1])) > 0) {
                    ctx.writeAndFlush(new AnnounceAction(request, this.session).call());
                } else {
                    ctx.close().sync();
                }
            }
            if (request.method() == RtspMethods.SETUP) {
                ctx.writeAndFlush(new SetupAction(request, this.session).call());
            }
            if (request.method() == RtspMethods.RECORD) {
                if (this.id != null) this.holderImpl.getStreamService().setActive(this.id, true);
                ctx.writeAndFlush(new RecordAction(request, this.session).call());
            }
            if (request.method() == RtspMethods.TEARDOWN) {
                if (this.id != null) this.holderImpl.getStreamService().setActive(this.id, false);
                ctx.writeAndFlush(new TeardownAction(request, this.session).call());
            }
        } else if (msg instanceof RtpPkt) {
            RtpPkt rtpPkt = (RtpPkt) msg;
            InterLeavedRTPSession rtpSession = session.getRTPSessions()[session.getStreamIndex(rtpPkt.getRtpChannle())];
            if (rtpPkt.getRtpChannle() == rtpSession.rtpChannel()) {
                if (rtpSession.getMediaStream().getMediaType() == MediaType.VIDEO) {
                    holderImpl.getSource().output().send(MessageBuilder.withPayload(rtpPkt.getRaw()).build());
                }
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        if (this.id != null) this.holderImpl.getStreamService().setActive(this.id, false);
    }
}
