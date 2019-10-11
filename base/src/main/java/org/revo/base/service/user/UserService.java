package org.revo.base.service.user;

import org.revo.base.domain.User;

import java.util.Optional;

public interface UserService {
    void save(User user);

    Optional<User> findByUsername(String username);

    long count();
}
