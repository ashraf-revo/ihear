package org.revo.ihear.livepoll.config.rtspHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.rtsp.RtspMethods;
import org.revo.ihear.livepoll.rtsp.RtspSession;
import org.revo.ihear.livepoll.rtsp.action.*;
import org.revo.ihear.livepoll.rtsp.d.InterLeavedRTPSession;
import org.revo.ihear.livepoll.rtsp.d.MediaType;
import org.revo.ihear.livepoll.rtsp.rtp.Encoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpNaluEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.base.NALU;
import org.revo.ihear.livepoll.rtsp.rtp.base.RtpPkt;
import org.revo.ihear.livepoll.rtsp.utils.URLObject;

import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static java.util.Base64.getDecoder;
import static org.springframework.messaging.support.MessageBuilder.withPayload;


public class RtspMessageHandler extends ChannelInboundHandlerAdapter {
    private HolderImpl holderImpl;
    private RtspSession session;
    private String id;
    private final Encoder<RtpPkt, NALU> rtpNaluEncoder = new RtpNaluEncoder();

    public RtspMessageHandler(HolderImpl holderImpl) {
        this.holderImpl = holderImpl;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        if (msg instanceof DefaultFullHttpRequest) {
            DefaultFullHttpRequest request = (DefaultFullHttpRequest) msg;
            if (request.method() == RtspMethods.OPTIONS) {
                ctx.writeAndFlush(new OptionsAction(request, this.session).call());
            }
            if (request.method() == RtspMethods.ANNOUNCE) {
                String sessionId = UUID.randomUUID().toString();
                this.id = URLObject.getId(request.uri());
                if (!this.holderImpl.getStreamService().findOneById(this.id).isPresent()) {
                    ctx.close().sync();
                }
                this.session = new RtspSession(request.uri()).setId(sessionId).withSdp(request.content().toString(StandardCharsets.UTF_8));
                try {
                    for (Object mediaDescription : session.getSessionDescription().getMediaDescriptions(true)) {
                        MediaDescription description = (MediaDescription) mediaDescription;
                        if (description.getMedia().getMediaType().equals("video")) {
                            if (description.getAttribute("rtpmap") == null || !description.getAttribute("rtpmap").contains("H264/90000")) {
                                ctx.close().sync();
                            }
                            String[] sprop = description.getAttribute("fmtp").split("; ")[1].replace("sprop-parameter-sets=", "").split(",");
                            if (sprop.length == 2)
                                this.holderImpl.getStreamService().setSpsPps(this.id, getDecoder().decode(sprop[0]), getDecoder().decode(sprop[1]));
                        }
                        if (description.getMedia().getMediaType().equals("audio")) {
                            if (description.getAttribute("rtpmap") == null || !description.getAttribute("rtpmap").contains("MPEG4-GENERIC")) {
                                ctx.close().sync();
                            }
                        }
                    }
                } catch (SdpException e) {
                    ctx.close().sync();
                }
                ctx.writeAndFlush(new AnnounceAction(request, this.session).call());
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
                    rtpNaluEncoder.encode(rtpPkt).forEach(it -> {
                        if (it.getNaluHeader().getTYPE() == 5) {
                            holderImpl.getStreamService().setIdr(id, it.getPayload());
                        }
                        if (it.getNaluHeader().getTYPE() == 6) {
                            holderImpl.getStreamService().setSei(id, it.getPayload());
                        }
                        if (it.getNaluHeader().getTYPE() == 7) {
                            holderImpl.getStreamService().setSps(id, it.getPayload());
                        }
                        if (it.getNaluHeader().getTYPE() == 8) {
                            holderImpl.getStreamService().setPps(id, it.getPayload());
                        }
                    });
                    synchronized (holderImpl.getSource().output()) {
                        holderImpl.getSource().output().send(withPayload(rtpPkt.getRaw()).setHeader("type", "Video").setHeader("streamId", id).build());
                    }
                }
                if (rtpSession.getMediaStream().getMediaType() == MediaType.AUDIO) {
                    synchronized (holderImpl.getSource().output()) {
                        holderImpl.getSource().output().send(withPayload(rtpPkt.getRaw()).setHeader("type", "Audio").setHeader("streamId", id).build());
                    }
                }
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        if (this.id != null) this.holderImpl.getStreamService().setActive(this.id, false);
    }
}
