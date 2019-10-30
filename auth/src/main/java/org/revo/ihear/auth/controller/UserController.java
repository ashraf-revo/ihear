package org.revo.ihear.auth.controller;

import org.revo.base.domain.User;
import org.revo.base.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @GetMapping("/user/change/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String change(@AuthenticationPrincipal User current, @PathVariable("id") String id) {
        userService.changeEnabled(current.getId(), id);
        return "redirect:/home";
    }

    @GetMapping("/user/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete(@AuthenticationPrincipal User current, @PathVariable("id") String id) {
        userService.delete(current.getId(), id);
        return "redirect:/home";
    }

    @GetMapping({"/","/home"})
    public ModelAndView home(@AuthenticationPrincipal User current) {
        ModelAndView modelMap = new ModelAndView("home");
        modelMap.addObject("users", userService.findByCreatedBy(current.getId()));
        return modelMap;
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, @AuthenticationPrincipal User current) {
        if (current != null && current.getAuthorities().stream().anyMatch(it -> "ROLE_ADMIN".equals(it.getAuthority()))) {
            user.setUsername(user.getUsername() + "@" + current.getUsername());
            user.setRoles("ROLE_USER");
            user.setEnabled(true);
            user.setCreatedBy(current.getId());
        } else {
            user.setRoles("ROLE_ANONYMOUS");
            user.setEnabled(false);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "signup";
    }
}
