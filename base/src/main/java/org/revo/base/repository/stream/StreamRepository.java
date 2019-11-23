package org.revo.base.repository.stream;

import org.revo.base.domain.Stream;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StreamRepository extends CrudRepository<Stream, String> {
    @Query(fields = "{'videoContent': 0 ,'audioContent': 0 }")
    List<Stream> findAllByCreateBy(String id);

    long countBySchemaId(String id);
}
