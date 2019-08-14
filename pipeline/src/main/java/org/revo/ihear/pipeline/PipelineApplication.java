package org.revo.ihear.pipeline;

import org.revo.base.domain.Action;
import org.revo.base.domain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding(Processor.class)
public class PipelineApplication {
    private Map<Integer, UnicastReceivingChannelAdapter> channelAdapterMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(PipelineApplication.class, args);
    }

    @Autowired
    private Processor processor;

    @StreamListener(Sink.INPUT)
    public void handle(Message<Action> action) {
        if (action.getPayload().getStatus() == Status.OPEN) {
            if (!channelAdapterMap.containsKey(action.getPayload().getPort())) {
                UnicastReceivingChannelAdapter unicastReceivingChannelAdapter = new UnicastReceivingChannelAdapter(action.getPayload().getPort());
                unicastReceivingChannelAdapter.setOutputChannel(processor.output());
                unicastReceivingChannelAdapter.start();
                channelAdapterMap.put(action.getPayload().getPort(), unicastReceivingChannelAdapter);
            }
        }
        if (action.getPayload().getStatus() == Status.CLOSE) {
            if (channelAdapterMap.containsKey(action.getPayload().getPort())) {
                channelAdapterMap.get(action.getPayload().getPort()).stop();
                channelAdapterMap.remove(action.getPayload().getPort());
            }
        }
//        {"status":"OPEN","port":7000}
    }


}

