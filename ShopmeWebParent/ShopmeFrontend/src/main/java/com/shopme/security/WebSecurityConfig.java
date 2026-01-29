package com.shopme.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final ShopmeCustomerUserDetailsService customerUserDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customerUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        (requests) -> requests
//                                .requestMatchers("/*.css", "/images/**", "/js/**", "/webjars/**", "/fontawesome/**", "/webfonts/**")
//                                .permitAll()
                                .requestMatchers("/account")
                                .authenticated()
                                .anyRequest()
                                .permitAll()
                )
                .formLogin(
                        (form) -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateCustomer")
                                .usernameParameter("email")
                                .permitAll()
                )
                .logout(
                        (logout) -> logout.permitAll()
                )
                .rememberMe(
                        // set a static key that not changed after restarting the app,
                        // instead of the auto generated one at every app startup
                        rem -> rem
                                .key("qhkd!sdf$bsv42sdsd54bjwbjk@1fsd") // private key to prevent rememberMe token modification
                                .tokenValiditySeconds(3 * 24 * 60 * 60)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
