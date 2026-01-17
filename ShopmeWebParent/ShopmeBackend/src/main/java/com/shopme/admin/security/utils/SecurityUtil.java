package com.shopme.admin.security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class to check if the current user has any of the allowed roles and not other roles.
 */
public class SecurityUtil {

    public static boolean hasAnyRoleOFAllowedRolesAndNotOther(String... allowedRoles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Set<String> userRoles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Set<String> allowed = Set.of(allowedRoles);

        boolean hasAllowed = userRoles.stream().anyMatch(allowed::contains);
        boolean hasNotAllowed = userRoles.stream().anyMatch(userRole -> !allowed.contains(userRole));

        return hasAllowed && !hasNotAllowed;
    }
}

