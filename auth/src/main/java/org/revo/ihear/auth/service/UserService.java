package org.revo.ihear.auth.service;

import org.revo.base.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void save(User user);

    Optional<User> findByUsername(String username);

    long count();

    List<User> findByCreatedBy(String id);

    void changeEnabled(String father, String child);

    void delete(String father, String child);
}
