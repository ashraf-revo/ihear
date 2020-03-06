package org.revo.ihear.auth.controller;

import org.revo.ihear.auth.service.ClientDetailsService;
import org.revo.ihear.auth.service.UserService;
import org.revo.ihear.entites.domain.ClientDetails;
import org.revo.ihear.entites.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientDetailsService clientDetailsService;

    @GetMapping("/user")
    @ResponseBody
    public Object user(OAuth2Authentication oAuth2Authentication) {
        return tokenStore.readAccessToken(((OAuth2AuthenticationDetails) oAuth2Authentication.getDetails()).getTokenValue()).getAdditionalInformation();
    }

    @PostMapping("/client")
    @ResponseBody
    public ClientDetails clientDetails(@RequestBody ClientDetails clientDetails) {
        clientDetails.setClientSecret(passwordEncoder.encode(clientDetails.getClientSecret()));
        return clientDetailsService.save(clientDetails);
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "signup";
    }
}
