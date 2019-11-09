package org.revo.ihear.auth.repository;

import org.revo.ihear.auth.domain.Key;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KeyRepository extends CrudRepository<Key, String> {
    List<Key> findAllByCreatedBy(String createdBy);

    void deleteByIdAndCreatedBy(String id, String createdBy);
}
