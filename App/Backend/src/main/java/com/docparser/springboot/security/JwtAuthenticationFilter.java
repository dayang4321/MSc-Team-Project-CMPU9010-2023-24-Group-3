package com.docparser.springboot.security;

import com.docparser.springboot.utils.SessionUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Retrieve the 'Authorization' header from the request
        Optional<String> token = Optional.ofNullable(request.getHeader("Authorization"));

        // Check if the token is present and starts with 'Bearer '
        if (token.isPresent() && token.get().startsWith("Bearer ")) {
            // Remove 'Bearer ' from the token
            token = Optional.of(token.get().substring(7));

            // Validate the JWT token
            if (SessionUtils.validateToken(token.get())) {
                // Create an Authentication object using the session ID from the token
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        SessionUtils.getSessionIdFromToken(token.get()), null, Collections.emptyList());

                // Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
