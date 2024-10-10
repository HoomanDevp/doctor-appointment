package com.stts.doctorappointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String DOCTOR_ROLE = "DOCTOR";
    private static final String[] PUBLIC_URLS = {
            "/available-all-bookings",
            "/available-bookings",
            "/book-appointment",
            "/patient-appointments",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };
    private static final String[] DOCTOR_URLS = {
            "/add-open-appointment",
            "/reserved-appointment",
            "/delete-open-appointment"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .headers().frameOptions().disable().and()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers(DOCTOR_URLS).hasRole(DOCTOR_ROLE)
                        .antMatchers(PUBLIC_URLS).permitAll()
                        .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetails() {
        UserDetails user = User
                .withUsername("stts")
                .password(passwordEncoder().encode("stts"))
                .roles("DOCTOR")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
