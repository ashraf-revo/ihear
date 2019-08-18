package org.revo.ihear.streamer.codec.base;


public abstract class Packet implements Raw, Payload {
    private Meta meta;

    Packet(Type type, RtpPkt rtpPkt) {
        if (type == Type.RTP) rtpPkt = ((RtpPkt) this);
        this.meta = Meta.from(type, rtpPkt);
    }

    public Meta getMeta() {
        return meta;
    }

    public enum Type {
        AUDIO, VIDEO, RTP
    }

    public static class Meta {
        private Type type;
        private int seqNumber;
        private long timeStamp;

        private Meta() {
        }


        static Meta from(Type type, RtpPkt rtpPkt) {
            Meta meta = new Meta();
            meta.seqNumber = rtpPkt.getSeqNumber();
            meta.timeStamp = rtpPkt.getTimeStamp();
            meta.type = type;
            return meta;
        }

        public int getSeqNumber() {
            return seqNumber;
        }


        public long getTimeStamp() {
            return timeStamp;
        }

        public Type getType() {
            return type;
        }
    }
}
