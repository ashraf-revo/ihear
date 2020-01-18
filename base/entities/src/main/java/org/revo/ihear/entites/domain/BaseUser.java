package org.revo.ihear.entites.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

public abstract class BaseUser implements UserDetails {
    private Collection<Authority> authorities = new ArrayList<>();

    public void setAuthorities(Collection<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities.stream().map(it -> ((GrantedAuthority) it)).collect(Collectors.toList());
    }

    @Transient
    @JsonProperty(access = WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Transient
    @JsonProperty(access = WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Transient
    @JsonProperty(access = WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
