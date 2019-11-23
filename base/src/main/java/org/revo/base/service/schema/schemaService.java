package org.revo.base.service.schema;

import org.revo.base.domain.Schema;

import java.util.Optional;

public interface schemaService {
    Optional<Schema> findOne(String id);
}
