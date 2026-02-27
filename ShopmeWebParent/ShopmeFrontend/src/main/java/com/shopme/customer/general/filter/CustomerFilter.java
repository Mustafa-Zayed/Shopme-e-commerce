//package com.shopme.customer.general.filter;
//
//import com.shopme.common.entity.Customer;
//import com.shopme.customer.general.service.CustomerService;
//import com.shopme.security.ShopmeCustomerUserDetails;
//import com.shopme.security.oauth2.CustomerOAuth2User;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
///**
// *  This filter is used to set the user full name in the request attribute
// *  so that it can be used in the navbar
// */
//@RequiredArgsConstructor
//@Component
//public class CustomerFilter implements Filter {
//    private final CustomerService customerService;
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest servletRequest = (HttpServletRequest) request;
//        String requestURL = servletRequest.getRequestURL().toString();
//
//        if (requestURL.contains(".css") || requestURL.contains(".js") ||
//                requestURL.contains(".png") || requestURL.contains(".jpg")
//                || requestURL.contains("data:image") || requestURL.contains("blob:") ||
//                requestURL.contains("/login")) {
//            chain.doFilter(request, response);
//            return;
//        }
//        String email = getEmail();
//        Customer customer = customerService.findByEmail(email);
//        request.setAttribute("userFullName", customer == null ? "" : customer.getFullName());
//
//        chain.doFilter(request, response);
//    }
//
//    private String getEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = authentication.getPrincipal();
////        System.out.println("Authentication.getPrincipal(): " + principal.toString());
//        String email = "";
//        if (principal instanceof CustomerOAuth2User customerOAuth2User) { // OAuth2 user
//            email = customerOAuth2User.getEmail();
//        } else if (principal instanceof ShopmeCustomerUserDetails shopmeCustomerUserDetails) {
//            email = shopmeCustomerUserDetails.getUsername();
//        }
//        return email;
//    }
//}
