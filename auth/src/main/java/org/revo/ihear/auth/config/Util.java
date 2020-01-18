package org.revo.ihear.auth.config;

import org.revo.ihear.auth.service.ClientDetailsService;
import org.revo.ihear.auth.service.UserService;
import org.revo.ihear.entites.config.Env;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.KeyPair;

@Configuration
public class Util {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return s -> userService.findByUsername(s).orElseThrow(() -> new UsernameNotFoundException("can't find this one "));
    }

    @Bean
    public TokenStore tokenStore(KeyPair keyPair) {
        return new JwtTokenStore(accessTokenConverter(keyPair));
    }

    @Bean
    public CommandLineRunner runner(UserService userService, ClientDetailsService clientDetailsService, Env env) {
        return args -> {
            if (userService.count() == 0) env.getUsers().stream().forEach(userService::save);
            if (clientDetailsService.count() == 0) env.getClientDetails().stream().forEach(clientDetailsService::save);
        };
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter(KeyPair keyPair) {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair);
        converter.setAccessTokenConverter(new AuthDefaultAccessTokenConverter());
        return converter;
    }
}

