package org.revo.ihear.auth.repository;

import org.revo.ihear.entites.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByUsername(String username);

    List<User> findByCreatedBy(String id);

    void deleteByCreatedByAndId(String father, String child);
}
