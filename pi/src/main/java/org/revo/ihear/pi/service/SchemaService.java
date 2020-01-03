package org.revo.ihear.pi.service;

import org.revo.ihear.pi.domain.Schema;

import java.util.List;
import java.util.Optional;

public interface SchemaService {
    Optional<Schema> findOneById(String id);

    Schema save(Schema schema);

    List<Schema> findAll();
}
