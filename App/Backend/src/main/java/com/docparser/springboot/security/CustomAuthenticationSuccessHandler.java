package com.docparser.springboot.security;

import com.docparser.springboot.utils.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

// Custom authentication success handler extending Spring Security's SimpleUrlAuthenticationSuccessHandler
@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // Injects the redirect URI from application properties
    @Value("${app.oauth2.redirectUri}")
    private String redirectUri;

    // Override the onAuthenticationSuccess method to handle the successful
    // authentication
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        handle(request, response, authentication);
        // Clears authentication attributes after handling
        super.clearAuthenticationAttributes(request);
    }

    // Custom handler method to redirect user after successful authentication
    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        // Chooses the target URL based on whether the redirectUri is configured
        String targetUrl = redirectUri.isEmpty() ? determineTargetUrl(request, response, authentication) : redirectUri;

        // Retrieves user details from authentication object
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // Generates a token for the session
        String token = SessionUtils.generateToken(user.getId(), SessionUtils.getTime(),
                SessionUtils.getExpirationTime());

        // Appends the token and expiry time as query parameters to the target URL
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam("expiry", SessionUtils.getExpirationTime().toInstant().toString())
                .build()
                .toUriString();

        // Redirects to the target URL
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
