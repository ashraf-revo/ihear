package org.revo.ihear.pi.service;

import org.revo.ihear.pi.domain.Device;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeviceService {

    Mono<Device> save(Device device);

    Mono<Device> findOneById(String id);

    Flux<Device> findAll(String id);
}
