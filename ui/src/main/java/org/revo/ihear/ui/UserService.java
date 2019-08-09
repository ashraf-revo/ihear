package org.revo.ihear.ui;

import reactor.core.publisher.Mono;

public interface UserService {
    Mono<String> current();
}
