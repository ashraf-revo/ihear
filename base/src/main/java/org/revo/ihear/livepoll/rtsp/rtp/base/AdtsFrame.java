package org.revo.ihear.livepoll.rtsp.rtp.base;

public class AdtsFrame extends Packet {
    private byte[] payload;
    private final static byte[] header = new byte[]{(byte) 0xFF, (byte) 0xF1, (byte) 0x4C, (byte) 0x80, (byte) 0x2F, (byte) 0x5F, (byte) 0xFC};

    //FF F1 5C 80 00 0F FC 21
//    FF F1 4C 80 00 1F FC
    public AdtsFrame(RtpPkt rtpPkt, byte[] payload, int offset, int size) {
        super(Type.AUDIO, rtpPkt);
        initSize(size);
        this.payload = new byte[size];
        System.arraycopy(payload, offset, this.payload, 0, this.payload.length);
    }


    private void initSize(int size) {
        size += header.length;
        header[3] |= (byte) ((size & 0x1800) >> 11);
        header[4] = (byte) ((size & 0x1FF8) >> 3);
        header[5] = (byte) ((size & 0x7) << 5);
    }

    public byte[] getHeader() {
        return header;
    }


    @Override
    public byte[] getRaw() {
        byte[] raw = new byte[payload.length + header.length];
        System.arraycopy(header, 0, raw, 0, header.length);
        System.arraycopy(payload, 0, raw, header.length, payload.length);
        return raw;
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
}
