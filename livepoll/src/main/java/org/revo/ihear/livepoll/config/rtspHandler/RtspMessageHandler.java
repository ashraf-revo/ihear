package org.revo.ihear.livepoll.config.rtspHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.rtsp.RtspMethods;
import org.revo.base.domain.StreamType;
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
    private int state = 0;
    private final Encoder<RtpPkt, NALU> rtpNaluEncoder = new RtpNaluEncoder();

    public RtspMessageHandler(HolderImpl holderImpl) {
        this.holderImpl = holderImpl;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        if (msg instanceof DefaultFullHttpRequest) {
            System.out.println(msg);
            DefaultFullHttpRequest request = (DefaultFullHttpRequest) msg;
            if (request.method() == RtspMethods.OPTIONS) {
                ctx.writeAndFlush(new OptionsAction(request, this.session).call());
                state = 1;
            }
            if (request.method() == RtspMethods.ANNOUNCE) {
                String sessionId = UUID.randomUUID().toString();
                this.id = URLObject.getId(request.uri());
                if (!this.holderImpl.getStreamService().findOneById(this.id).isPresent()) {
                    ctx.close().sync();
                }
                String sdp = request.content().toString(StandardCharsets.UTF_8);
                this.session = new RtspSession(request.uri()).setId(sessionId).withSdp(sdp);
                String[] sprop = new String[]{};
                boolean haveAudio = false;
                boolean haveVideo = false;
                try {
                    for (Object mediaDescription : session.getSessionDescription().getMediaDescriptions(true)) {
                        MediaDescription description = (MediaDescription) mediaDescription;
                        if (description.getMedia().getMediaType().equals("video")) {
                            if (description.getAttribute("rtpmap") == null || !description.getAttribute("rtpmap").contains("H264/90000")) {
                                ctx.close().sync();
                            }
                            sprop = description.getAttribute("fmtp").split("; ")[1].replace("sprop-parameter-sets=", "").split(",");
                            haveVideo = true;
                        }
                        if (description.getMedia().getMediaType().equals("audio")) {
                            if (description.getAttribute("rtpmap") == null || !description.getAttribute("rtpmap").contains("MPEG4-GENERIC")) {
                                ctx.close().sync();
                            }
                            haveAudio = true;
                        }
                    }
                } catch (SdpException e) {
                    ctx.close().sync();
                }
                if (haveAudio && haveVideo) {
                    this.holderImpl.getStreamService().setSdp(this.id, sdp, StreamType.BOOTH);
                } else if (haveAudio) {
                    this.holderImpl.getStreamService().setSdp(this.id, sdp, StreamType.AUDIO);
                } else if (haveVideo) {
                    this.holderImpl.getStreamService().setSdp(this.id, sdp, StreamType.VIDEO);
                    if (sprop.length == 2)
                        this.holderImpl.getStreamService().setSpsPps(this.id, getDecoder().decode(sprop[0]), getDecoder().decode(sprop[1]));
                } else {
                    this.holderImpl.getStreamService().setSdp(this.id, sdp, StreamType.UNKNOWN);
                }
                ctx.writeAndFlush(new AnnounceAction(request, this.session).call());
                state = 2;
            }
            if (request.method() == RtspMethods.SETUP) {
                ctx.writeAndFlush(new SetupAction(request, this.session).call());
                state = 3;
            }
            if (request.method() == RtspMethods.RECORD) {
                ctx.writeAndFlush(new RecordAction(request, this.session).call());
                state = 4;
            }
            if (request.method() == RtspMethods.TEARDOWN) {
                ctx.writeAndFlush(new TeardownAction(request, this.session).call());
                state = 5;
            }
        } else if (msg instanceof RtpPkt) {
            if (state != 4)
                ctx.close().sync();
            RtpPkt rtpPkt = (RtpPkt) msg;
            InterLeavedRTPSession rtpSession = session.getRTPSessions()[session.getStreamIndex(rtpPkt.getRtpChannle())];
            if (rtpPkt.getRtpChannle() == rtpSession.rtpChannel()) {
                if (rtpSession.getMediaStream().getMediaType() == MediaType.VIDEO) {
                    synchronized (rtpNaluEncoder) {
                        rtpNaluEncoder.encode(rtpPkt).forEach(it -> {
                            if (it.getNaluHeader().getTYPE()!=1) System.out.println(it.getNaluHeader().getTYPE());
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
                    }
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
    }
}
