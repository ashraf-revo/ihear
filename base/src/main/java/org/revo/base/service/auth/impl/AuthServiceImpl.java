package org.revo.base.service.auth.impl;

import org.revo.base.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private ReactiveJwtDecoder reactiveJwtDecoder;
    @Autowired
    private Environment environment;

    @Override
    public Mono<String> currentJwtUserId() {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).map(Authentication::getPrincipal)
                .cast(Jwt.class).map(Jwt::getClaims)
                .map(it -> it.get("user"))
                .map(it -> (HashMap<String, String>) it)
                .flatMapMany(it -> Flux.fromStream(it.entrySet().stream()))
                .filter(it -> it.getKey().equals("id"))
                .map(Map.Entry::getValue)
                .next();
    }

    @Override
    public Mono<String> currentJwtUserId(String token) {
        return currentJwt(token)
                .map(Jwt::getClaims)
                .map(it -> it.get("user"))
                .map(it -> (HashMap<String, String>) it)
                .flatMapMany(it -> Flux.fromStream(it.entrySet().stream()))
                .filter(it -> it.getKey().equals("id"))
                .map(Map.Entry::getValue)
                .next();
    }

    @Override
    public Mono<Jwt> currentJwt(String token) {
        return reactiveJwtDecoder.decode(token);
    }

    @Override
    public Mono<String> currentOAuth2User() {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).map(Authentication::getPrincipal)
                .cast(DefaultOAuth2User.class).map(DefaultOAuth2User::getAttributes)
                .map(it -> it.get("user"))
                .map(it -> (HashMap<String, String>) it)
                .flatMapMany(it -> Flux.fromStream(it.entrySet().stream()))
                .filter(it -> it.getKey().equals("id"))
                .map(Map.Entry::getValue)
                .next();
    }

    @Override
    public Mono<Authentication> currentAuthentication() {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);
    }


    public Mono<String> remoteUser(String session) {
        return WebClient.create((Arrays.asList(environment.getActiveProfiles()).stream()
                .anyMatch("kubernetes"::equals) ? "http://ui" : "http://localhost:8080"))
                .get()
                .uri("/auth/user")
                .cookie("SESSION", session)
                .retrieve()
                .bodyToMono(String.class);
    }

}
