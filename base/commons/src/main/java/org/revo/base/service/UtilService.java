package org.revo.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class UtilService {
    @Autowired
    private Environment environment;
    @Value("${org.revo.base.env.url:localhost:8080}")
    private String defaultBaseUrl;
    private boolean isCloud = false;

    @PostConstruct
    public void init() {
        this.isCloud = Arrays.asList(environment.getActiveProfiles()).contains("cloud");
    }

    public String getBaseUrl() {
        return isCloud ? "lb://ui" : defaultBaseUrl;
    }
}
