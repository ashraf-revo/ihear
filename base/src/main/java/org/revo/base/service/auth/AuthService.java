package org.revo.base.service.auth;

import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<String> currentJwtUser();
    Mono<String> currentJwtUser(String token);

    Mono<String> currentOAuth2User();
}
