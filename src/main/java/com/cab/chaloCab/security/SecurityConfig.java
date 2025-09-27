    package com.cab.chaloCab.security;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.cors.*;
    
    import java.util.List;
    
    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    @Slf4j
    public class SecurityConfig {
    
        private final JwtAuthFilter jwtAuthFilter;
        private final UserDetailsService userDetailsService;
        private final PasswordEncoder passwordEncoder;
    
        // Centralized public endpoints list
        public static final String[] PUBLIC_ENDPOINTS = {
                "/", "/index", "/health",
                "/api/auth/**",
                "/api/admin/dashboard/summary", "/api/admin/dashboard/summary/**",
                "/api/customers/**",
                "/api/flutter/**",
                "/api/bookings/**",
                "/api/fares/**",
                "/api/driver-requests/submit"
        };
    
        @Bean
        public AuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService);
            authProvider.setPasswordEncoder(passwordEncoder);
            return authProvider;
        }
    
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
    
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
    
                            // Admin-only routes
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/drivers/**").hasRole("ADMIN")
                            .requestMatchers("/api/driver-requests/**").hasRole("ADMIN")
                            //.requestMatchers("/api/fares/**").hasRole("ADMIN")
                            .requestMatchers("/api/ride/assign/**").hasRole("ADMIN")
    
                            // Driver-only routes
                            .requestMatchers("/api/ride/accept/**").hasRole("DRIVER")
                            .requestMatchers("/api/trip/complete/**").hasRole("DRIVER")
                            .requestMatchers("/api/trip/history/driver/**").hasRole("DRIVER")
    
                            // User-only routes
                            .requestMatchers("/api/user/**").hasRole("USER")
                            .requestMatchers("/api/trip/history/customer/**").hasRole("USER")
    
                            // Shared routes
                            //.requestMatchers("/api/bookings/**").hasAnyRole("ADMIN", "USER")
                            .requestMatchers("/api/cabs/**").hasAnyRole("ADMIN", "USER")
                            .requestMatchers("/api/payments/**").hasAnyRole("ADMIN", "USER")
                            .requestMatchers("/api/notifications/**").hasAnyRole("ADMIN", "USER", "DRIVER")
                           // .requestMatchers("/api/driver-requests/submit").permitAll()
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authenticationProvider())
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
            log.info("✅ SecurityFilterChain configured successfully.");
            return http.build();
        }
    
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedOriginPattern("*");
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            log.info("✅ CORS configuration registered.");
            return source;
        }
    }
