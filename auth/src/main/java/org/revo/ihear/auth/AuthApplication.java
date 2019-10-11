package org.revo.ihear.auth;

import org.revo.base.config.Env;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"org.revo.base.config", "org.revo.base.service.user", "org.revo.base.repository.user", "org.revo.ihear.auth"})
@EnableMongoRepositories(basePackages = {"org.revo.base.repository.user", "org.revo.ihear.auth"})
@EnableConfigurationProperties(Env.class)
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
