package org.revo.base.service.schema.impl;

import org.revo.base.domain.Schema;
import org.revo.base.repository.schema.SchemaRepository;
import org.revo.base.service.schema.schemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class schemaServiceImpl implements schemaService {
    @Autowired
    private SchemaRepository schemaRepository;

    public Optional<Schema> findOne(String id) {
        return schemaRepository.findById(id);
    }
}
