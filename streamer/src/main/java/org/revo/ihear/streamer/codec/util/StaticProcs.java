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
package org.revo.ihear.streamer.codec.util;

/**
 * Generic functions for converting between unsigned integers and byte[]s.
 *
 * @author Arne Kepp
 */
public class StaticProcs {
    public static byte[] int2Byte(int i) {
        final byte[] bystes = new byte[4];
        bystes[0] = (byte) ((0xFF000000 & i) >> 24);
        bystes[1] = (byte) ((0xFF0000 & i) >> 16);
        bystes[2] = (byte) ((0xFF00 & i) >> 8);
        bystes[3] = (byte) ((0xFF & i));
        return bystes;
    }

    public static int bytesToUIntInt(byte[] bytes, int index) {
        int accum = 0;
        int i = 1;
        for (int shiftBy = 0; shiftBy < 16; shiftBy += 8) {
            accum |= ((long) (bytes[index + i] & 0xff)) << shiftBy;
            i--;
        }
        return accum;
    }

    public static long bytesToUIntLong(byte[] bytes, int index) {
        long accum = 0;
        int i = 3;
        for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
            accum |= ((long) (bytes[index + i] & 0xff)) << shiftBy;
            i--;
        }
        return accum;
    }

}