package org.revo.ihear.auth.config;

import org.revo.ihear.entites.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.LinkedHashMap;
import java.util.Map;

public class AuthDefaultAccessTokenConverter extends DefaultAccessTokenConverter {
    public AuthDefaultAccessTokenConverter() {
        setUserTokenConverter(new DefaultUserAuthenticationConverter() {
            @Override
            public Map<String, ?> convertUserAuthentication(Authentication authentication) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("user", authentication.getPrincipal());
                response.put("sub", ((User) authentication.getPrincipal()).getId());
                if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
                    response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
                }
                return response;
            }
        });
    }

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Map<String, Object> map = (Map<String, Object>) super.convertAccessToken(token, authentication);
        if (authentication.isClientOnly() && !map.containsKey("sub")) {
            map.put("sub", authentication.getOAuth2Request().getClientId());
        }
        return map;
    }

}
