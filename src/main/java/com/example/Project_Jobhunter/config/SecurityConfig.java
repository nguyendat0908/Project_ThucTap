package com.example.Project_Jobhunter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint)
                        throws Exception {
                String[] whiteList = { "/", "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/register" };
                http
                                .csrf((csrf) -> csrf.disable())
                                .authorizeHttpRequests((requests) -> requests
                                                .requestMatchers(whiteList).permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
                                                .authenticationEntryPoint(customAuthenticationEntryPoint)) // Bắt
                                                                                                           // exception
                                                                                                           // khi token
                                                                                                           // không hợp
                                                                                                           // lệ
                                .formLogin((form) -> form.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                return http.build();
        }
}
