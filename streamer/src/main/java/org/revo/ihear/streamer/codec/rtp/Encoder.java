package org.revo.ihear.streamer.codec.rtp;

import java.util.List;

public interface Encoder<IN, OUT> {
    List<OUT> encode(IN in);

    List<IN> decode(List<OUT> out);
}
