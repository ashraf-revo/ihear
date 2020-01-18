package org.revo.ihear.service.auth.impl;

import org.revo.ihear.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @Override
    public Mono<String> currentJwtUserId() {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).map(Authentication::getPrincipal)
                .cast(Jwt.class).map(Jwt::getSubject);
//                .map(it -> (HashMap<String, String>) it)
//                .flatMapMany(it -> Flux.fromStream(it.entrySet().stream()))
//                .filter(it -> it.getKey().equals("id"))
//                .map(Map.Entry::getValue)
//                .next();
    }

    @Override
    public Mono<String> currentJwtUserId(String token) {
        return currentJwt(token).map(Jwt::getSubject);
    }

    @Override
    public Mono<Jwt> currentJwt(String token) {
        return reactiveJwtDecoder.decode(token);
    }

    @Override
    public Mono<String> currentOAuth2User() {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).map(Authentication::getPrincipal)
                .cast(DefaultOAuth2User.class).map(DefaultOAuth2User::getAttributes)
                .map(it -> it.get(JwtClaimNames.SUB).toString());
    }

    @Override
    public Mono<Authentication> currentAuthentication() {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
    }
}
