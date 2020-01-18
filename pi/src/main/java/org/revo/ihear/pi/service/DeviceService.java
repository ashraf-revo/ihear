package org.revo.ihear.pi.service;

import org.revo.ihear.pi.domain.Device;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface DeviceService {

    Mono<Device> save(Device device);

    Optional<Device> findOneById(String id);

    List<Device> findAll(String id);
}
