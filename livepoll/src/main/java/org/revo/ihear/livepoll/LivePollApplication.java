package org.revo.ihear.livepoll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"org.revo.base.config", "org.revo.base.service.auth", "org.revo.base.service.stream", "org.revo.base.repository.stream", "org.revo.ihear.livepoll"})
@EnableMongoRepositories(basePackages = {"org.revo.base.repository.stream", "org.revo.ihear.livepoll"})
@EnableBinding(Source.class)
@EnableFeignClients
public class LivePollApplication {
    public static void main(String[] args) {
        SpringApplication.run(LivePollApplication.class, args);
    }

}
