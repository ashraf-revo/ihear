package org.revo.ihear.auth.config;

import org.revo.base.config.Env;
import org.revo.base.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private KeyPair keyPair;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return s -> userService.findByUsername(s).orElseThrow(() -> new UsernameNotFoundException("can't find this one "));
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public CommandLineRunner runner(UserService userService, org.revo.ihear.auth.service.ClientDetailsService clientDetailsService, Env env) {
        return args -> {
            if (userService.count() == 0 && env.getUsers().size() > 0) env.getUsers().forEach(userService::save);
            if (clientDetailsService.count() == 0 && env.getClientDetails().size() > 0)
                env.getClientDetails().forEach(clientDetailsService::save);
        };
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(this.keyPair);
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
