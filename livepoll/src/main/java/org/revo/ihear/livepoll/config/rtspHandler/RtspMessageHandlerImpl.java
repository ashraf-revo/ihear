package org.revo.ihear.livepoll.config.rtspHandler;

import gov.nist.javax.sdp.fields.AttributeField;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.rtsp.RtspMethods;
import org.revo.base.domain.StreamType;
import org.revo.ihear.livepoll.rtsp.RtspSession;
import org.revo.ihear.livepoll.rtsp.d.InterLeavedRTPSession;
import org.revo.ihear.livepoll.rtsp.d.MediaType;
import org.revo.ihear.livepoll.rtsp.rtp.Encoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpAdtsFrameEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpNaluEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpUtil;
import org.revo.ihear.livepoll.rtsp.rtp.base.AdtsFrame;
import org.revo.ihear.livepoll.rtsp.rtp.base.NALU;
import org.revo.ihear.livepoll.rtsp.rtp.base.RtpPkt;
import org.revo.ihear.livepoll.rtsp.utils.URLObject;
import org.revo.ihear.livepoll.util.SdpUtil;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.messaging.support.MessageBuilder.withPayload;

public class RtspMessageHandlerImpl extends RtspMessageHandler {
    private HttpMethod state;
    private String streamId;
    private RtspSession session;
    private HolderImpl holderImpl;
    private final Encoder<RtpPkt, NALU> rtpNaluEncoder = new RtpNaluEncoder();
    private final Encoder<RtpPkt, AdtsFrame> rtpAdtsFrameEncoder = new RtpAdtsFrameEncoder();
    private List<String> spropParameter = new ArrayList<>();

    public RtspMessageHandlerImpl(HolderImpl holderImpl) {
        this.holderImpl = holderImpl;
    }

    @Override
    void handleRtsp(ChannelHandlerContext ctx, DefaultFullHttpRequest request) {
//            if (!this.holderImpl.getAuthorizationCheck().apply(request)
//                    .onErrorResume(throwable -> Mono.empty())
//                    .blockOptional().isPresent()) {
//                close(ctx, "not authorized ");
//            }
        if (request.method() == RtspMethods.ANNOUNCE) {
            this.streamId = URLObject.getId(request.uri());
            this.session = new RtspSession(request.uri()).withSdp(request.content().toString(StandardCharsets.UTF_8));
            Map<String, List<AttributeField>> rtpmap = SdpUtil.
                    getAttributeFields(this.session.getSessionDescription(), "rtpmap");
            StreamType streamType = SdpUtil.getStreamType(rtpmap.keySet());
            if (SdpUtil.isSupported(rtpmap)) {
                if (streamType == StreamType.VIDEO || streamType == StreamType.BOOTH) {
                    spropParameter = SdpUtil.getSpropParameter(this.session.getSessionDescription());
                    if (spropParameter.size() == 0) {
                        close(ctx, "Sorry Unsupported Stream");
                    }
                    sendSpsPps();
                }
            } else {
                close(ctx, "Sorry Unsupported Stream");
            }
        }
    }

    @Override
    void handleRtp(InterLeavedRTPSession rtpSession, RtpPkt rtpPkt) {
        synchronized (holderImpl.getPiSource().output1()) {
            if (rtpSession.getMediaStream().getMediaType() == MediaType.VIDEO) {
                rtpNaluEncoder.encode(rtpPkt).forEach(it ->
                        holderImpl.getPiSource().output1().send(buildMessage(it.getPayload(), rtpSession.getMediaStream().getMediaType())));
            }
            if (rtpSession.getMediaStream().getMediaType() == MediaType.AUDIO) {
                rtpAdtsFrameEncoder.encode(rtpPkt).forEach(it ->
                        holderImpl.getPiSource().output1().send(buildMessage(it.getPayload(), rtpSession.getMediaStream().getMediaType())));
            }
        }
    }

    @Override
    void setState(HttpMethod state) {
        this.state = state;
    }

    @Override
    HttpMethod getState() {
        return state;
    }

    @Override
    RtspSession getSession() {
        return session;
    }

    private Message<byte[]> buildMessage(byte[] payload, MediaType type) {
        return withPayload(payload)
                .setHeader("type", type)
                .setHeader("streamId", streamId).build();
    }

    private void sendSpsPps() {
        spropParameter.stream().flatMap(it -> Arrays.asList(it.split(",")).stream())
                .map(RtpUtil::toNalu).map(it -> buildMessage(it.getPayload(), MediaType.VIDEO))
                .forEach(it -> holderImpl.getPiSource().output2().send(it));
    }
}
