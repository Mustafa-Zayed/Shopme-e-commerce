package com.shopme.security.oauth2;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.general.service.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) authentication.getPrincipal();

        String name = customerOAuth2User.getName();
        String email = customerOAuth2User.getEmail();
        String countryCode = request.getLocale().getCountry();

        String clientName = customerOAuth2User.clientName();
        AuthenticationType authenticationType = getAuthenticationType(clientName);

        Customer byEmail = customerService.findByEmail(email);
        if (byEmail == null) {
            customerService.saveCustomerUponOAuth2Login(name, email, countryCode, authenticationType);
        } else {
            // Show the full name of the principal from database instead of from OAuth2User
            // If we don't do this, the full name of the last logged in user will be displayed, not the current one.
            customerOAuth2User.setFullName(byEmail.getFullName());
            customerService.updateAuthenticationType(byEmail, authenticationType);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        AuthenticationType authenticationType;
        if (clientName.equals("Google")) {
            authenticationType = AuthenticationType.GOOGLE;
        } else if (clientName.equals("Facebook")) {
            authenticationType = AuthenticationType.FACEBOOK;
        } else {
            authenticationType = AuthenticationType.DATABASE;
        }
        return authenticationType;
    }
}
