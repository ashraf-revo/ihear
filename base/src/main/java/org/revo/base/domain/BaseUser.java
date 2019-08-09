package org.revo.base.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;




import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

public abstract class BaseUser implements UserDetails {
    private String roles = "ROLE_ANONYMOUS";

    @Transient
    @JsonProperty(access = WRITE_ONLY)
    @Override
    public Collection<CustomGrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(getRoles().split(",")).stream().map(CustomGrantedAuthority::new).collect(Collectors.toList());
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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
