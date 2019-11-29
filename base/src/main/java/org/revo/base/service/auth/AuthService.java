package org.revo.base.service.auth;

import org.revo.base.domain.Stream;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<String> currentJwtUserId();

    Mono<String> currentJwtUserId(String token);

    Mono<Jwt> currentJwt(String token);

    Mono<String> currentOAuth2User();

    Mono<Authentication> currentAuthentication();

    Mono<String> remoteUser(String session);

    Mono<Stream> remoteStream(String session, String streamId);

}
