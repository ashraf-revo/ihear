package org.revo.ihear.pi.repository;

import org.revo.ihear.pi.domain.Device;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DeviceRepository extends ReactiveMongoRepository<Device, String> {
    Flux<Device> findAllByCreatedBy(String id);
}
