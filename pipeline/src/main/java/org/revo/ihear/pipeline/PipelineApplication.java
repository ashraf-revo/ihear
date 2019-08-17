package org.revo.ihear.pipeline;

import org.revo.base.domain.Action;
import org.revo.base.domain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding(Processor.class)
public class PipelineApplication {
    @Autowired
    private Processor processor;
    private Map<Integer, UnicastReceivingChannelAdapter> channelAdapterMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(PipelineApplication.class, args);
    }


    @StreamListener(Sink.INPUT)
    public void handle(Message<Action> action) {
        if (action.getPayload().getStatus() == Status.CREATE && !channelAdapterMap.containsKey(action.getPayload().getPort())) {
            UnicastReceivingChannelAdapter unicastReceivingChannelAdapter = new UnicastReceivingChannelAdapter(action.getPayload().getPort());
            unicastReceivingChannelAdapter.setOutputChannel(processor.output());
            channelAdapterMap.put(action.getPayload().getPort(), unicastReceivingChannelAdapter);
        }
        if (action.getPayload().getStatus() == Status.START && channelAdapterMap.containsKey(action.getPayload().getPort()))
            channelAdapterMap.get(action.getPayload().getPort()).start();
        if (action.getPayload().getStatus() == Status.STOP && channelAdapterMap.containsKey(action.getPayload().getPort()))
            channelAdapterMap.get(action.getPayload().getPort()).stop();
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            handle(MessageBuilder.withPayload(new Action(Status.CREATE, 3000)).build());
            handle(MessageBuilder.withPayload(new Action(Status.START, 3000)).build());

        };
    }
}

