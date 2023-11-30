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

    private final UserService userService;
    private final GoogleOAuth2UserInfoExtractor oAuth2UserInfoExtractor;

    public CustomOAuth2UserService(UserService userService, GoogleOAuth2UserInfoExtractor oAuth2UserInfoExtractor) {
        this.userService = userService;
        this.oAuth2UserInfoExtractor = oAuth2UserInfoExtractor;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        CustomUserDetails customUserDetails = oAuth2UserInfoExtractor.extractUserInfo(oAuth2User);
        UserAccount user = upsertUser(customUserDetails);
        return customUserDetails;
    }

    private UserAccount upsertUser(CustomUserDetails customUserDetails) {

       Optional<UserAccount> userOptional = userService.fetchUserById(customUserDetails.getId());
        UserAccount user;
        if (userOptional.isEmpty()) {
            user = new UserAccount();
            user.setUserId(customUserDetails.getId());
            user.setUsername(customUserDetails.getName());
            user.setEmail(customUserDetails.getEmail());
            user.setProvider(customUserDetails.getProvider());
        } else {
            user = userOptional.get();
            user.setEmail(customUserDetails.getEmail());
        }
        userService.saveUser(user);
        return user;
    }
}
