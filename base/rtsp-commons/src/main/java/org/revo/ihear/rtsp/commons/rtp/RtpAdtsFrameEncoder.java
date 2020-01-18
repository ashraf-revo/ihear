package org.revo.ihear.rtsp.commons.rtp;



import org.revo.ihear.rtsp.commons.rtp.base.AdtsFrame;
import org.revo.ihear.rtsp.commons.rtp.base.RtpPkt;
import org.revo.ihear.rtsp.commons.utils.StaticProcs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class RtpAdtsFrameEncoder implements Encoder<RtpPkt, AdtsFrame> {
    private RtpToAdtsFrame rtpToAdtsFrame = new RtpToAdtsFrame();
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public List<AdtsFrame> encode(RtpPkt rtpPkt) {
        return rtpToAdtsFrame.apply(rtpPkt);
    }

    @Override
    public int incAndGet() {
        return atomicInteger.incrementAndGet();
    }

    private class RtpToAdtsFrame implements Function<RtpPkt, List<AdtsFrame>> {
        @Override
        public List<AdtsFrame> apply(RtpPkt rtpPkt) {
            List<AdtsFrame> adts = new ArrayList<>();
            int auHeaderLength = StaticProcs.bytesToUIntInt(rtpPkt.getPayload(), 0) >> 3;
            int offset = 2 + auHeaderLength;
            for (int i = 0; i < (auHeaderLength / 2); i++) {
                int size = StaticProcs.bytesToUIntInt(rtpPkt.getPayload(), 2 + (i * 2)) >> 3;
                adts.add(new AdtsFrame(rtpPkt.getPayload(), offset, size));
                offset += size;
            }
            return adts;
        }

    }
}
