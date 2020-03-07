package org.revo.ihear.auth.service;

import org.revo.ihear.entites.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void save(User user);

    Optional<User> findByUsername(String username);

    long count();

    List<User> findAll();
}
