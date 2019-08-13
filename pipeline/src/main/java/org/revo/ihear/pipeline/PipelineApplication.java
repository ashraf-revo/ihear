package org.revo.ihear.pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@ComponentScan(basePackages = {"org.revo.base", "org.revo.ihear.pipeline"})
@EnableMongoRepositories(basePackages = {"org.revo.base", "org.revo.ihear.pipeline"})
@EnableDiscoveryClient
@EnableWebFluxSecurity
@EnableBinding(Processor.class)
public class PipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(PipelineApplication.class, args);
    }

}
