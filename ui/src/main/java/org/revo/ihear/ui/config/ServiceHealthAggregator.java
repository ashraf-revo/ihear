package org.revo.ihear.ui.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class ServiceHealthAggregator implements ReactiveHealthIndicator {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private GatewayProperties gatewayProperties;
    private WebClient webClient = WebClient.builder().build();

    @Override
    public Mono<Health> health() {
        return checkDownstreamServiceHealth().onErrorResume(
                ex -> Mono.just(new Health.Builder().down(ex).build())
        );
    }

    private Mono<Health> checkDownstreamServiceHealth() {
        List<Mono<AbstractMap.SimpleImmutableEntry<String, HealthEntry>>> down =
                gatewayProperties.getRoutes().stream().collect(Collectors.toMap(it -> it.getId(), it -> {
                    List<ServiceInstance> instances = discoveryClient.getInstances(it.getId());
                    return instances.stream().map(ite -> {
                        String uri = it.getUri().toString();
                        String ptr = "://" + it.getId();
                        String path = uri.substring(uri.indexOf(ptr) + (ptr).length());
                        return ite.getUri() + path + "/actuator/health";
                    }).collect(toList());
                })).entrySet().stream().flatMap(it ->
                        it.getValue().stream().map(itn -> webClient.get().uri(itn).exchange().flatMap(tt ->
                                tt.bodyToMono(HealthEntry.class))
                                .onErrorReturn(new HealthEntry("down")).map(sm ->
                                        new AbstractMap.SimpleImmutableEntry<>(it.getKey() + "-" + itn, sm))))
                        .collect(toList());
        return Flux.merge(down)
                .collectList()
                .map(v -> v.stream().collect(Collectors.groupingBy(it -> it.getKey().split("-")[0])))
                .map(itx -> new Health.Builder().withDetails(itx).up().build());
    }
}

class HealthEntry {
    private String status;

    public HealthEntry() {
    }

    public HealthEntry(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
