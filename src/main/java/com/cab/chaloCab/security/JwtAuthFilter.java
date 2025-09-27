package com.cab.chaloCab.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Allow preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);

        String subject = null;
        String role = null;

        try {
            if (!jwtUtil.validateToken(token)) {
                chain.doFilter(request, response);
                return;
            }
            subject = jwtUtil.getSubjectFromToken(token);  // phone (for customers) or email (for admins)
            role = jwtUtil.getRoleFromToken(token);        // ADMIN/DRIVER/USER/CUSTOMER
        } catch (Exception e) {
            // Token invalid/expired -> let Spring Security handle 401 downstream
            chain.doFilter(request, response);
            return;
        }

        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            String normalizedRole = (role != null && !role.trim().isEmpty()) ? role.trim().toUpperCase() : "USER";
            List<SimpleGrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + normalizedRole));

            // 🔑 Set subject (phone/email) directly as principal, so authentication.getName() returns it
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(subject, null, authorities);

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}
