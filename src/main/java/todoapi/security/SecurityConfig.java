package todoapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Why this file exists: Tells Spring Security exactly which endpoints are public and which require a token.


@Configuration  // ← this class contains Spring configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // CSRF protection is for browser sessions — APIs use tokens instead

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // STATELESS = don't store sessions, every request must have a token

                .authorizeHttpRequests(auth -> auth
                        // ✅ These endpoints are PUBLIC — no token needed
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // 🔒 Everything else requires a valid token
                        .anyRequest().authenticated()
                )

                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable()))
                // Needed for H2 console to display in browser

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // Run our JwtFilter before Spring's default filter

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // BCrypt is a strong hashing algorithm for passwords
        // "password123" → "$2a$10$xyz..." (one-way, can never be reversed)
    }
}