package org.revo.base.domain;

public class VideoContent extends Content {
    // type=7
    private byte[] sps;
    // type=8
    private byte[] pps;

    public byte[] getSps() {
        return sps;
    }

    public void setSps(byte[] sps) {
        this.sps = sps;
    }

    public byte[] getPps() {
        return pps;
    }

    public void setPps(byte[] pps) {
        this.pps = pps;
    }

}
