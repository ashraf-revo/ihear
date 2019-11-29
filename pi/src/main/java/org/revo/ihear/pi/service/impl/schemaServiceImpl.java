package org.revo.ihear.pi.service.impl;

import org.revo.ihear.pi.domain.Schema;
import org.revo.ihear.pi.repository.SchemaRepository;
import org.revo.ihear.pi.service.schemaService;
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
