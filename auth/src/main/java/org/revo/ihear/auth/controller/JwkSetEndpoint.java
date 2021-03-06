package org.revo.ihear.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

@FrameworkEndpoint
public class JwkSetEndpoint {
    @Autowired
    private KeyPair keyPair;

    @GetMapping("/.well-known/jwks.json")
    @ResponseBody
    public Object getKey() {
        return new JWKSet(new RSAKey.Builder((RSAPublicKey) this.keyPair.getPublic()).build()).toJSONObject();
    }
}