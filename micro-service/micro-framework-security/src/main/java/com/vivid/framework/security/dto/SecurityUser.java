package com.vivid.framework.security.dto;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Map;

public class SecurityUser extends User {
    private String appId;
    private Map<String, Object> additionalInformation;

    public String getAppId() {
        return appId;
    }

    public Map<String, Object> getAdditionalInformation() {
        return additionalInformation;
    }

    /// @Getter
    /// private String clientId;

    public SecurityUser(String appId, Map<String, Object> additionalInformation,
                        String username, String password, boolean enabled, boolean accountNonExpired,
                        boolean credentialsNonExpired, boolean accountNonLocked, Collection<?
            extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.appId = appId;
        this.additionalInformation = additionalInformation;
    }

    //@JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    //@JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    //@JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }

    //@JsonIgnore
    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }
}
