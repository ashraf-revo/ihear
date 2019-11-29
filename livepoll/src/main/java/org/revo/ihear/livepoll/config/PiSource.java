package org.revo.ihear.livepoll.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PiSource {
    String OUTPUT1 = "output1";

    @Output(PiSource.OUTPUT1)
    MessageChannel output1();

    String OUTPUT2 = "output2";

    @Output(PiSource.OUTPUT2)
    MessageChannel output2();
}
