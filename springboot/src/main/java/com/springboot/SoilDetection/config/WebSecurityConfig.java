package com.springboot.SoilDetection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors().and()
                                .csrf().disable()
                                .authorizeRequests(authorize -> authorize
                                                .antMatchers(
                                                                "/",
                                                                "/index.html",
                                                                "/favicon.ico",
                                                                "/manifest.json",
                                                                "/assets/**",
                                                                "/static/**",
                                                                "/error")
                                                .permitAll()
                                                .antMatchers("/api/**").permitAll()
                                                .anyRequest().permitAll());

                return http.build();
        }
}
