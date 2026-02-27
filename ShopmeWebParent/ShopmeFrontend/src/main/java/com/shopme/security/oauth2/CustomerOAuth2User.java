package com.shopme.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public record CustomerOAuth2User(OAuth2User oAuth2User, String clientName) implements OAuth2User {
    static String fullName;

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }

    public String getFullName() {
//        return oAuth2User.getAttribute("name");
        return CustomerOAuth2User.fullName == null ? oAuth2User.getAttribute("name") : CustomerOAuth2User.fullName;
    }

    public void setFullName(String fullName) {
        CustomerOAuth2User.fullName = fullName;
    }
}
