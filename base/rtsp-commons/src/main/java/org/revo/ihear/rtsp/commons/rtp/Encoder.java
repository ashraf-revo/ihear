package org.revo.ihear.rtsp.commons.rtp;

import java.util.List;

public interface Encoder<IN, OUT> {
    List<OUT> encode(IN in);

    int incAndGet();
}
