package com.shopme.admin.security;

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

    private final ShopmeUserDetailsService shopmeUserDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(shopmeUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        (requests) -> requests
                                .requestMatchers("/*.css", "/images/**", "/js/**", "/webjars/**", "/fontawesome/**", "/webfonts/**")
                                .permitAll()
                                .requestMatchers("/users/**").hasAuthority("Admin")
                                .requestMatchers("/categories/**").hasAnyAuthority("Admin", "Editor")
                                .requestMatchers("/brands/**").hasAnyAuthority("Admin", "Editor")
                                .requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
                                .requestMatchers("/customers/**").hasAnyAuthority("Admin", "Salesperson")
                                .requestMatchers("/shipping/**").hasAnyAuthority("Admin", "Salesperson")
                                .requestMatchers("/orders/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                                .requestMatchers("/reports/**").hasAnyAuthority("Admin", "Salesperson")
                                .requestMatchers("/articles/**").hasAnyAuthority("Admin", "Editor")
                                .requestMatchers("/menus/**").hasAnyAuthority("Admin", "Editor")
                                .requestMatchers("/settings/**").hasAuthority("Admin")
                                .anyRequest()
                                .authenticated()
                )
                .formLogin(
                        (form) -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateUser")
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
