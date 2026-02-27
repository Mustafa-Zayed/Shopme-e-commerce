package com.shopme.security;

import com.shopme.security.oauth2.CustomerOAuth2UserService;
import com.shopme.security.oauth2.OAuth2LoginSuccessHandler;
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
    private final CustomerOAuth2UserService customerOAuth2UserService;
    // circular dependency issue, so we will inject them as method parameters in filterChain method.
//    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
//    private final DatabaseLoginSuccessHandler databaseLoginSuccessHandler;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customerUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                                           DatabaseLoginSuccessHandler databaseLoginSuccessHandler) throws Exception {
        http
                .authorizeHttpRequests(
                        (requests) -> requests
//                                .requestMatchers("/*.css", "/images/**", "/js/**", "/webjars/**", "/fontawesome/**", "/webfonts/**")
//                                .permitAll()
                                .requestMatchers("/account/**")
                                .authenticated()
                                .anyRequest()
                                .permitAll()
                )
                .formLogin(
                        (form) -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateCustomer")
                                .usernameParameter("email")
                                .successHandler(databaseLoginSuccessHandler)
                                .permitAll()
                )
                .oauth2Login(
                        (oauth2) -> oauth2
                                .loginPage("/login")
                                .userInfoEndpoint(
                                        (userInfo) -> userInfo
                                                .userService(customerOAuth2UserService)
                                )
                                .successHandler(
                                        oAuth2LoginSuccessHandler
                                )
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
