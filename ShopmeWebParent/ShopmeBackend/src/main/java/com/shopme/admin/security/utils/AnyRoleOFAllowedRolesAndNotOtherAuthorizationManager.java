package com.shopme.admin.security.utils;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

/**
 * Authorization manager used in SecurityFilterChain that checks if the current user has any of
 * the allowed roles and not other roles.
 * @param ALLOWED_ROLES
 */
public record AnyRoleOFAllowedRolesAndNotOtherAuthorizationManager(String... ALLOWED_ROLES)
        implements AuthorizationManager<RequestAuthorizationContext> {

    public AnyRoleOFAllowedRolesAndNotOtherAuthorizationManager(String... ALLOWED_ROLES) {
        this.ALLOWED_ROLES = ALLOWED_ROLES.clone();
    }

    @Override
    public AuthorizationDecision check(
            Supplier<Authentication> authentication,
            RequestAuthorizationContext context) {

        Authentication auth = authentication.get();

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        boolean granted = SecurityUtil.hasAnyRoleOFAllowedRolesAndNotOther(ALLOWED_ROLES);

        return new AuthorizationDecision(granted);
    }
}
