package org.revo.ihear.livepoll.rtsp.rtp.base;


public class NALU extends Packet {
    private byte[] payload;
    private NaluHeader naluHeader;
    private final static byte[] header = {0x00, 0x00, 0x00, 0x01};

    public NALU(byte[] payload, int offset, int length) {
        this.payload = new byte[length - offset];
        System.arraycopy(payload, offset, this.payload, 0, this.payload.length);
        this.naluHeader = NaluHeader.read(payload[offset]);
    }


    public NALU(int F, int NRI, int TYPE) {
        this.payload = new byte[1];
        this.naluHeader = NaluHeader.from(F, NRI, TYPE);
        this.payload[0] = this.naluHeader.getRaw();
    }

    public void appendPayload(byte[] data, int offset) {
        byte[] ndata = new byte[data.length - offset];
        System.arraycopy(data, offset, ndata, 0, ndata.length);
        this.payload = copyOfAndAppend(this.payload, ndata);
    }

    private static byte[] copyOfAndAppend(byte[] data1, byte[] data2) {
        byte[] result = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, result, 0, data1.length);
        System.arraycopy(data2, 0, result, data1.length, data2.length);
        return result;
    }

    public NaluHeader getNaluHeader() {
        return naluHeader;
    }

    @Override
    public byte[] getRaw() {
        byte[] bytes = new byte[header.length + payload.length];
        System.arraycopy(header, 0, bytes, 0, header.length);
        System.arraycopy(payload, 0, bytes, header.length, payload.length);
        return bytes;
    }

    public static byte[] getRaw(byte[] payload) {
        byte[] bytes = new byte[header.length + payload.length];
        System.arraycopy(header, 0, bytes, 0, header.length);
        System.arraycopy(payload, 0, bytes, header.length, payload.length);
        return bytes;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    public static class NaluHeader {
        private int F;
        private int NRI;
        private int TYPE;

        private NaluHeader() {

        }

        NaluHeader(int F, int NRI, int TYPE) {
            this.F = F;
            this.NRI = NRI;
            this.TYPE = TYPE;
        }

        public static NaluHeader read(byte b) {
            NaluHeader naluHeader = new NaluHeader();
            naluHeader.F = (b & 0x80) >> 7;
            naluHeader.NRI = (b & 0x60) >> 5;
            naluHeader.TYPE = b & 0x1F;
            return naluHeader;
        }

        @Override
        public String toString() {
            return "NaluHeader{" +
                    "F=" + F +
                    ", NRI=" + NRI +
                    ", TYPE=" + TYPE +
                    '}';
        }

        static NaluHeader from(int F, int NRI, int TYPE) {
            return new NaluHeader(F, NRI, TYPE);
        }

        public int getF() {
            return F;
        }

        public int getNRI() {
            return NRI;
        }

        public int getTYPE() {
            return TYPE;
        }

        byte getRaw() {
            int i = ((this.F << 7) | (this.NRI << 5) | (this.TYPE & 0x1F)) & 0xFF;
            return ((byte) i);
        }
    }
}
