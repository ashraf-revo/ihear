package org.revo.base.domain;

public class VideoContent extends Content {
    // type=5
    private byte[] idr;
    // type=6
    private byte[] sei;
    // type=7
    private byte[] sps;
    // type=8
    private byte[] pps;

    public byte[] getIdr() {
        return idr;
    }

    public void setIdr(byte[] idr) {
        this.idr = idr;
    }

    public byte[] getSei() {
        return sei;
    }

    public void setSei(byte[] sei) {
        this.sei = sei;
    }

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
