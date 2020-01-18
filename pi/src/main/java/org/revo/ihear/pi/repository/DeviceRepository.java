package org.revo.ihear.pi.repository;

import org.revo.ihear.pi.domain.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeviceRepository extends CrudRepository<Device, String> {
    List<Device> findAllByCreatedBy(String id);
}
