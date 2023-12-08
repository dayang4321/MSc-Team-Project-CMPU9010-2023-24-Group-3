package com.docparser.springboot.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleOAuth2UserInfoExtractor {

    // Method to extract user details from OAuth2User object
    public CustomUserDetails extractUserInfo(OAuth2User oAuth2User) {
        CustomUserDetails customUserDetails = new CustomUserDetails();

        // Setting User-specific details to the CustomUserDetails Object
        customUserDetails.setUsername(retrieveAttr("email", oAuth2User));
        customUserDetails.setId(retrieveAttr("sub", oAuth2User));
        customUserDetails.setName(retrieveAttr("name", oAuth2User));
        customUserDetails.setEmail(retrieveAttr("email", oAuth2User));
        customUserDetails.setAvatarUrl(retrieveAttr("picture", oAuth2User));
        customUserDetails.setProvider("google");
        customUserDetails.setAttributes(oAuth2User.getAttributes());
        customUserDetails.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority("USER")));
        return customUserDetails;
    }

    // Helper method to retrieve specific attribute from OAuth2User
    private String retrieveAttr(String attr, OAuth2User oAuth2User) {
        Object attribute = oAuth2User.getAttributes().get(attr);
        return attribute == null ? "" : attribute.toString();
    }
}
