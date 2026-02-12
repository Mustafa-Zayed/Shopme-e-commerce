package com.shopme.security.oauth2;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomerOAuth2UserService extends DefaultOAuth2UserService {

    /**
     * Load user from OAuth2 provider, invoked by Spring Security after successful authorization
     * @param userRequest OAuth2 user request
     * @return OAuth2 user
     * @throws OAuth2AuthenticationException if authorization fails
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return new CustomerOAuth2User(oAuth2User);
    }
}
