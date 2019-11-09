package org.revo.ihear.auth.controller;

import org.revo.base.domain.User;
import org.revo.base.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user")
    @ResponseBody
    public Object user(OAuth2Authentication oAuth2Authentication) {
        return tokenStore.readAccessToken(((OAuth2AuthenticationDetails) oAuth2Authentication.getDetails()).getTokenValue()).getAdditionalInformation();
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, @AuthenticationPrincipal User current) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "signup";
    }
}
