/**
 * Java RTP Library (jlibrtp)
 * Copyright (C) 2006 Arne Kepp
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.revo.ihear.livepoll.rtsp.rtp.base;


import org.revo.ihear.livepoll.rtsp.utils.StaticProcs;

import java.util.Comparator;

public class RtpPkt extends Packet implements Comparator<RtpPkt>, Comparable<RtpPkt> {
    private int version = 2;        //2 bits
    private int padding;            //1 bit
    private int extension = 0;        //1 bit
    private int marker = 0;            //1 bit
    private int payloadType;        //
    private int seqNumber;            //16 bits
    private long timeStamp;            //32 bits
    private long ssrc;                //32 bits
    private long[] csrcArray = null;//
    private byte[] raw = null;
    private byte[] payload;
    private int rtpChannle;

    public RtpPkt(int rtpChannle, byte[] raw) {
        super(Type.RTP, null);
        this.rtpChannle = rtpChannle;
        if (raw == null) {
            System.out.println("RtpPkt(byte[]) Packet null");
        }

        int remOct = raw.length - 12;
        if (remOct >= 0) {
            this.raw = raw;    //Store it
            sliceFirstLine();
            if (version == 2) {
                sliceTimeStamp();
                sliceSSRC();
                if (remOct > 4 && getCsrcCount() > 0) {
                    sliceCSRCs();
                    remOct -= csrcArray.length * 4; //4 octets per CSRC
                }
                // TODO Extension
                if (remOct > 0) {
                    slicePayload(remOct);
                }

                //Sanity checks

                //Mark the buffer as current
            } else {
                System.out.println("RtpPkt(byte[]) Packet is not version 2, giving up.");
            }
        } else {
            System.out.println("RtpPkt(byte[]) Packet too small to be sliced");
        }
    }


    public int getRtpHeaderLength() {
        //TODO include extension
        return 12 + 4 * getCsrcCount();
    }

    private int getPayloadLength() {
        return payload.length;
    }


    protected int getVersion() {
        return version;
    }


    public boolean isMarked() {
        return (marker != 0);
    }

    public int getPayloadType() {
        return payloadType;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getSsrc() {
        return ssrc;
    }


    public int getCsrcCount() {
        if (csrcArray != null) {
            return csrcArray.length;
        } else {
            return 0;
        }
    }

    public long[] getCsrcArray() {
        return csrcArray;
    }


    private void sliceFirstLine() {
        version = ((this.raw[0] & 0xC0) >>> 6);
        padding = ((this.raw[0] & 0x20) >>> 5);
        extension = ((this.raw[0] & 0x10) >>> 4);
        csrcArray = new long[(this.raw[0] & 0x0F)];
        marker = ((this.raw[1] & 0x80) >> 7);
        payloadType = (this.raw[1] & 0x7F);
        seqNumber = StaticProcs.bytesToUIntInt(this.raw, 2);
    }

    private void sliceTimeStamp() {
        timeStamp = StaticProcs.bytesToUIntLong(this.raw, 4);
    }

    private void sliceSSRC() {
        ssrc = StaticProcs.bytesToUIntLong(this.raw, 8);
    }

    private void sliceCSRCs() {
        for (int i = 0; i < csrcArray.length; i++) {
            ssrc = StaticProcs.bytesToUIntLong(this.raw, i * 4 + 12);
        }
    }

    private void slicePayload(int bytes) {
        payload = new byte[bytes];
        int headerLen = getRtpHeaderLength();

        System.arraycopy(this.raw, headerLen, payload, 0, bytes);
    }

    public int getRtpChannle() {
        return rtpChannle;
    }

    public RtpPkt setRtpChannle(int rtpChannle) {
        this.rtpChannle = rtpChannle;
        return this;
    }

    @Override
    public int compare(RtpPkt o1, RtpPkt o2) {
        long a = o1.getSeqNumber();
        long b = o2.getSeqNumber();

        if (a == b)
            return 0;
        else if (a > b) {
            if (a - b < 32768)
                return 1;
            else
                return -1;
        } else // a < b
        {
            if (b - a < 32768)
                return -1;
            else
                return 1;
        }
    }

    @Override
    public int compareTo(RtpPkt o) {
        return compare(this, o);
    }

    @Override
    public byte[] getRaw() {
        return this.raw;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }
}
