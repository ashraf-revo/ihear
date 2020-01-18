package org.revo.ihear.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<String> currentJwtUserId();

    Mono<String> currentJwtUserId(String token);

    Mono<Jwt> currentJwt(String token);

    Mono<String> currentOAuth2User();

    Mono<Authentication> currentAuthentication();

}
