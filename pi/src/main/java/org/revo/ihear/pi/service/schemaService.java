package org.revo.ihear.pi.service;

import org.revo.ihear.pi.domain.Schema;

import java.util.Optional;

public interface schemaService {
    Optional<Schema> findOne(String id);
}
