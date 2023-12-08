package com.docparser.springboot.security;

import com.docparser.springboot.model.UserAccount;
import com.docparser.springboot.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // Dependencies for the user service and OAuth2 user info extractor
    private final UserService userService;
    private final GoogleOAuth2UserInfoExtractor oAuth2UserInfoExtractor;

    // Constructor to inject dependencies
    public CustomOAuth2UserService(UserService userService, GoogleOAuth2UserInfoExtractor oAuth2UserInfoExtractor) {
        this.userService = userService;
        this.oAuth2UserInfoExtractor = oAuth2UserInfoExtractor;
    }

    // Override the loadUser method to customize the OAuth2 user loading process
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // Delegate to the parent class to load user details
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extract custom user details from OAuth2 user information
        CustomUserDetails customUserDetails = oAuth2UserInfoExtractor.extractUserInfo(oAuth2User);

        // Update or create a new user in the system based on extracted details
        UserAccount user = upsertUser(customUserDetails);

        // Return the custom user details for further processing
        return customUserDetails;
    }

    // Private helper method to update or insert a new user
    private UserAccount upsertUser(CustomUserDetails customUserDetails) {
        // Fetch existing user based on the provided ID
        Optional<UserAccount> userOptional = userService.fetchUserById(customUserDetails.getId());
        UserAccount user;

        // If the user does not exist, create a new user account
        if (userOptional.isEmpty()) {
            user = new UserAccount();
            user.setUserId(customUserDetails.getId());
            user.setUsername(customUserDetails.getName());
            user.setEmail(customUserDetails.getEmail());
            user.setProvider(customUserDetails.getProvider());
        } else {
            // If user exists, update their email
            user = userOptional.get();
            user.setEmail(customUserDetails.getEmail());
        }

        // Save the user (either updated or new) to the database and return the user
        userService.saveUser(user);
        return user;
    }
}
