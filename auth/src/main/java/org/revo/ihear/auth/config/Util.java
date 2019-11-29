package org.revo.ihear.auth.config;

import org.revo.base.config.Env;
import org.revo.ihear.auth.service.UserService;
import org.revo.ihear.auth.service.ClientDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.KeyPair;
import java.util.LinkedHashMap;
import java.util.Map;

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
            env.getUsers().stream().filter(it -> userService.count() == 0).forEach(userService::save);
            env.getClientDetails().stream().filter(it -> clientDetailsService.count() == 0).forEach(clientDetailsService::save);
        };
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter(KeyPair keyPair) {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair);
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(new DefaultUserAuthenticationConverter() {
            @Override
            public Map<String, ?> convertUserAuthentication(Authentication authentication) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("user", authentication.getPrincipal());
                response.put("sub", authentication.getName());
                if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
                    response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
                }
                return response;
            }
        });
        converter.setAccessTokenConverter(accessTokenConverter);
        return converter;
    }
}
