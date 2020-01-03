package org.revo.ihear.pi.service.impl;

import org.revo.ihear.pi.domain.Schema;
import org.revo.ihear.pi.repository.SchemaRepository;
import org.revo.ihear.pi.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SchemaServiceImpl implements SchemaService {
    @Autowired
    private SchemaRepository schemaRepository;

    public Optional<Schema> findOneById(String id) {
        return schemaRepository.findById(id);
    }

    @Override
    public Schema save(Schema schema) {
        return schemaRepository.save(schema);
    }

    @Override
    public List<Schema> findAll() {
        return StreamSupport.stream(schemaRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}
