package org.revo.ihear.auth.controller;

import org.revo.ihear.auth.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class KeyController {
    @Autowired
    private KeyService keyService;

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView modelMap = new ModelAndView("home");
        modelMap.addObject("keys", keyService.findAll());
        return modelMap;
    }


    @GetMapping("/key/exist/{id}")
    @ResponseBody
    public ResponseEntity findOne(@PathVariable("id") String id) {
        return keyService.exist(id) ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();
    }

    @GetMapping("/key/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete(@PathVariable("id") String id) {
        keyService.delete(id);
        return "redirect:/home";
    }

    @GetMapping("/key/generate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<OAuth2AccessToken> generate() {
        OAuth2AccessToken oAuth2AccessToken = keyService.generateDeviceJwtToken();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + oAuth2AccessToken.getAdditionalInformation().get("jti") + ".key")
                .body(oAuth2AccessToken);
    }

}
