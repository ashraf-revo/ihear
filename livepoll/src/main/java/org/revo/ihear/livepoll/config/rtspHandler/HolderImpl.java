package org.revo.ihear.livepoll.config.rtspHandler;

import org.revo.base.service.stream.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.stereotype.Component;

@Component
public class HolderImpl {
    @Autowired
    private Source source;
    @Autowired
    private StreamService streamService;

    public Source getSource() {
        return source;
    }

    public StreamService getStreamService() {
        return streamService;
    }
}
