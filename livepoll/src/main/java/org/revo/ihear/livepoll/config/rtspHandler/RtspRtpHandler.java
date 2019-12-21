package org.revo.ihear.livepoll.config.rtspHandler;

import gov.nist.javax.sdp.fields.AttributeField;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.rtsp.RtspMethods;
import org.reactivestreams.Publisher;
import org.revo.base.domain.StreamType;
import org.revo.ihear.livepoll.rtsp.RtspSession;
import org.revo.ihear.livepoll.rtsp.action.*;
import org.revo.ihear.livepoll.rtsp.d.InterLeavedRTPSession;
import org.revo.ihear.livepoll.rtsp.d.MediaType;
import org.revo.ihear.livepoll.rtsp.rtp.Encoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpAdtsFrameEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpNaluEncoder;
import org.revo.ihear.livepoll.rtsp.rtp.RtpUtil;
import org.revo.ihear.livepoll.rtsp.rtp.base.AdtsFrame;
import org.revo.ihear.livepoll.rtsp.rtp.base.NALU;
import org.revo.ihear.livepoll.rtsp.rtp.base.RtpPkt;
import org.revo.ihear.livepoll.util.SdpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.springframework.messaging.support.MessageBuilder.withPayload;

public class RtspRtpHandler implements Function<Object, Publisher<?>> {
    private RtspHandler rtspHandler;
    private RtpHandler rtpHandler;


    public RtspRtpHandler(HolderImpl holderImpl) {
        this.rtspHandler = new RtspHandler(holderImpl);
        this.rtpHandler = new RtpHandler(holderImpl);
    }

    @Override
    public Publisher<?> apply(Object o) {
        if (o instanceof DefaultFullHttpRequest) {
            return rtspHandler.apply(((DefaultFullHttpRequest) o));
        }
        if (o instanceof RtpPkt && this.rtspHandler.getState() == RtspMethods.RECORD) {
            return rtpHandler.apply((RtpPkt) o, this.rtspHandler.getSession());
        }// else       close(signal, "not follwing rtsp seqance (OPTIONS,ANNOUNCE,SETUP,RECORD,TEARDOWN)");
        return rtspHandler.error;
    }


    private Message<byte[]> buildMessage(RtpPkt rtpPkt, byte[] payload, MediaType type) {
        return withPayload(payload)
                .setHeader("type", type)
                .setHeader("streamId", rtspHandler.getSession().getStreamId())
                .setHeader("seqNumber", rtpPkt != null ? rtpPkt.getSeqNumber() : 0)
                .setHeader("timeStamp", rtpPkt != null ? rtpPkt.getTimeStamp() : 0)
                .build();
    }

    private class RtspHandler implements Function<DefaultFullHttpRequest, Mono<?>> {
        private HttpMethod state;
        private RtspSession session;
        private final Mono<?> error = Mono.error(() -> new RuntimeException());
        private HolderImpl holderImpl;
        private final Logger logger = LoggerFactory.getLogger(RtspHandler.class);

        public RtspHandler(HolderImpl holderImpl) {
            this.holderImpl = holderImpl;
        }

        @Override
        public Mono<?> apply(DefaultFullHttpRequest request) {
            if (request.method() == RtspMethods.OPTIONS) {
                this.state = RtspMethods.OPTIONS;
                return Mono.just(new OptionsAction(request, this.session).call());
            } else if (this.state == RtspMethods.OPTIONS && request.method() == RtspMethods.ANNOUNCE) {
                this.state = RtspMethods.ANNOUNCE;
                return initSession(request).map(it -> new AnnounceAction(request, this.session).call());
            } else if ((this.state == RtspMethods.ANNOUNCE || this.state == RtspMethods.SETUP) && request.method() == RtspMethods.SETUP) {
                this.state = RtspMethods.SETUP;
                return Mono.just(new SetupAction(request, this.session).call());
            } else if (this.state == RtspMethods.SETUP && request.method() == RtspMethods.RECORD) {
                this.state = RtspMethods.RECORD;
                return Mono.just(new RecordAction(request, this.session).call());
            } else if (request.method() == RtspMethods.TEARDOWN) {
                this.state = RtspMethods.TEARDOWN;
                return Mono.just(new TeardownAction(request, this.session).call());
            } else return error;
//                    close(request, "not follwing rtsp seqance (OPTIONS,ANNOUNCE,SETUP,RECORD,TEARDOWN)");
        }

        private Mono<?> initSession(DefaultFullHttpRequest request) {
            return this.holderImpl.getAuthorizationCheck().apply(request).flatMap(it -> {
                if (request.method() == RtspMethods.ANNOUNCE) {
                    this.session = RtspSession.from(request);
                    logger.info(this.session.getSdp());
                    Map<String, List<AttributeField>> rtpmap = SdpUtil.getAttributeFields(this.session.getSessionDescription(), "rtpmap");
                    if (SdpUtil.isSupported(rtpmap)) {
                        StreamType streamType = SdpUtil.getStreamType(rtpmap.keySet());
                        if (streamType == StreamType.VIDEO || streamType == StreamType.BOOTH) {
                            List<String> spropParameter = SdpUtil.getSpropParameter(this.session.getSessionDescription());
                            if (spropParameter.size() == 0) {
                                return error;
//                            close(request, "Sorry Unsupported Stream");
                            }
                            spropParameter.stream().flatMap(its -> Arrays.asList(its.split(",")).stream())
                                    .map(RtpUtil::toNalu).map(its -> buildMessage(null, its.getPayload(), MediaType.VIDEO))
                                    .forEach(its -> holderImpl.getPiSource().output2().send(its));
                        }
                    } else {
                        return error;
//                    close(request, "Sorry Unsupported Stream");
                    }
                }
                // default return empty
                return Mono.just(true);
            });
        }

        public HttpMethod getState() {
            return state;
        }

        public RtspSession getSession() {
            return session;
        }
    }

    private class RtpHandler implements BiFunction<RtpPkt, RtspSession, Mono<?>> {
        private final Encoder<RtpPkt, NALU> rtpNaluEncoder = new RtpNaluEncoder();
        private final Encoder<RtpPkt, AdtsFrame> rtpAdtsFrameEncoder = new RtpAdtsFrameEncoder();
        private final Mono<?> empty = Mono.empty();
        private HolderImpl holderImpl;

        public RtpHandler(HolderImpl holderImpl) {
            this.holderImpl = holderImpl;
        }

        @Override
        public Mono<?> apply(RtpPkt rtpPkt, RtspSession session) {

            InterLeavedRTPSession rtpSession = session.getRTPSessions()[session.getStreamIndex(rtpPkt.getRtpChannle())];
            if (rtpPkt.getRtpChannle() == rtpSession.rtpChannel()) {
                if (rtpSession.getMediaStream().getMediaType() == MediaType.VIDEO) {
                    rtpNaluEncoder.encode(rtpPkt).forEach(it ->
                            holderImpl.getPiSource().output1().send(buildMessage(rtpPkt, it.getPayload(), rtpSession.getMediaStream().getMediaType())));
                }
                if (rtpSession.getMediaStream().getMediaType() == MediaType.AUDIO) {
                    rtpAdtsFrameEncoder.encode(rtpPkt).forEach(it ->
                            holderImpl.getPiSource().output1().send(buildMessage(rtpPkt, it.getPayload(), rtpSession.getMediaStream().getMediaType())));
                }
            }

            return empty;
        }
    }
}
