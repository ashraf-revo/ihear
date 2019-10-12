package org.revo.ihear.livepoll.rtsp.rtp;


import org.revo.ihear.livepoll.rtsp.rtp.base.NALU;
import org.revo.ihear.livepoll.rtsp.rtp.base.RtpPkt;
import org.revo.ihear.livepoll.rtsp.utils.StaticProcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class RtpNaluEncoder implements Encoder<RtpPkt, NALU> {

    private RtpToNalu rtpToNalu = new RtpToNalu();

    @Override
    public List<NALU> encode(RtpPkt rtpPkt) {
        return rtpToNalu.apply(rtpPkt);
    }

    @Override
    public List<RtpPkt> decode(List<NALU> nalus) {
        return null;
    }

    private class RtpToNalu implements Function<RtpPkt, List<NALU>> {
        private NALU fuNalU = null;

        @Override
        public List<NALU> apply(RtpPkt rtpPkt) {
            NALU.NaluHeader naluHeader = NALU.NaluHeader.read(rtpPkt.getPayload()[0]);
            if (naluHeader.getTYPE() > 0 && naluHeader.getTYPE() <= 23) {
                return Collections.singletonList(new NALU(rtpPkt, rtpPkt.getPayload(), 0, rtpPkt.getPayload().length));
            } else if (naluHeader.getTYPE() == 24) {
                List<NALU> nalus = new ArrayList<>();
                int offset = 1;
                while (offset < rtpPkt.getPayload().length - 1 /*NAL Unit-0 Header*/) {
                    int size = StaticProcs.bytesToUIntInt(rtpPkt.getPayload(), offset);
                    offset += 2;   //                NAL Unit-i Size
                    nalus.add(new NALU(rtpPkt, rtpPkt.getPayload(), offset, size + offset));
                    offset += size;//                NAL Unit-i Data
                }
                return nalus;
            } else if (naluHeader.getTYPE() == 28) {
                boolean start = ((rtpPkt.getPayload()[1] & 0x80) >> 7) > 0;
                boolean end = ((rtpPkt.getPayload()[1] & 0x40) >> 6) > 0;
//            int reserved = (rtpPkt.getPayload()[1] & 0x20) >> 5;
                int type = (rtpPkt.getPayload()[1] & 0x1F);
                if (start) {
                    this.fuNalU = new NALU(rtpPkt, naluHeader.getF(), naluHeader.getNRI(), type);
                    this.fuNalU.appendPayload(rtpPkt.getPayload(), 2);
                }
                if (this.fuNalU != null && this.fuNalU.getNaluHeader().getTYPE() == type) {
                    if (!start) {
                        this.fuNalU.appendPayload(rtpPkt.getPayload(), 2);
                    }
                    if (end) {
                        List<NALU> nalus = Collections.singletonList(new NALU(rtpPkt, fuNalU.getPayload(), 0, fuNalU.getPayload().length));
                        this.fuNalU = null;
                        return nalus;
                    }
                }
            } else {
                System.out.println("Unknown Type " + naluHeader.getTYPE());
            }
            return Collections.emptyList();
        }
    }

}

