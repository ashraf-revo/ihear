package org.revo.base.service;

import org.revo.base.domain.User;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface UserService {
    void save(User user);

    Optional<User> findByUsername(String username);

    long count();

    Mono<String> current();
}
