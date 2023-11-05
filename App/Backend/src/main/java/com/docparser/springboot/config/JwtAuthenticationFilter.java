package com.docparser.springboot.config;

import com.docparser.springboot.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SessionService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws  ServletException, IOException {
        Optional<String> token= Optional.ofNullable(request.getHeader("Authorization"));

        if ( !token.isEmpty() && token.get().startsWith("Bearer ")) {
            token = Optional.of(token.get().substring(7));

            // validate the token
            if ( jwtTokenService.validateToken(token.get())) {
               Authentication authentication = new UsernamePasswordAuthenticationToken(jwtTokenService.getSessionIdFromToken(token.get()), null, Collections.emptyList());
               SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
