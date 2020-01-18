package org.revo.ihear.entites.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    ROLE_ANONYMOUS,
    ROLE_USER,
    ROLE_DEVICE,
    ROLE_MICRO,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return this.toString();
    }
}
