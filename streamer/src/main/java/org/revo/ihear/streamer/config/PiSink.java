package org.revo.ihear.streamer.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface PiSink {
    String INPUT1 = "input1";

    @Input(PiSink.INPUT1)
    SubscribableChannel input1();

    String INPUT2 = "input2";

    @Input(PiSink.INPUT2)
    SubscribableChannel input2();
}
