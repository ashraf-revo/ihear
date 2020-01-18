package org.revo.ihear.entites.repository.stream;

import org.revo.ihear.entites.domain.Stream;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StreamRepository extends CrudRepository<Stream, String> {
    @Query(fields = "{'videoContent': 0 }")
    List<Stream> findAllByCreatedBy(String id);

    long countBySchemaId(String id);
}
