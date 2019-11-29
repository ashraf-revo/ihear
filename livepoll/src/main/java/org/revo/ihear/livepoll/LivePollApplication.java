package org.revo.ihear.livepoll;

import org.revo.ihear.livepoll.config.PiSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"org.revo.base.config", "org.revo.base.service.auth", "org.revo.ihear.livepoll"})
@EnableBinding(PiSource.class)
public class LivePollApplication {
    public static void main(String[] args) {
        SpringApplication.run(LivePollApplication.class, args);
    }

}
