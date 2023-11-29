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

@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {



    @Value("${app.oauth2.redirectUri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        handle(request, response, authentication);
        super.clearAuthenticationAttributes(request);
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = redirectUri.isEmpty() ?
                determineTargetUrl(request, response, authentication) : redirectUri;
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String token = SessionUtils.generateToken(user.getId(),SessionUtils.getTime(),SessionUtils.getExpirationTime());
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl).queryParam("token", token).queryParam("expiry",SessionUtils.getExpirationTime().toInstant().toString()).build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
