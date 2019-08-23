package org.revo.ihear.auth.controller;

import org.revo.ihear.auth.service.ClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@SessionAttributes(types = AuthorizationRequest.class)
public class OauthController {
    @Autowired
    private ClientDetailsService clientDetailsService;

    @GetMapping("/oauth/confirm_access")
    public ModelAndView getAccessConfirmation(@ModelAttribute AuthorizationRequest clientAuth, Principal user) {
        return new ModelAndView("confirmation").addObject("auth_request", clientAuth)
                .addObject("client", clientDetailsService.findByClientId(clientAuth.getClientId()).get());
    }

}
