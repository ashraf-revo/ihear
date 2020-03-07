package org.revo.ihear.auth.controller;

import org.junit.jupiter.api.Test;
import org.revo.ihear.auth.service.ClientDetailsService;
import org.revo.ihear.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JwkSetEndpoint.class)
public class JwkSetEndpointTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private ClientDetailsService clientDetailsService;
    @MockBean
    private UserService userService;

    @Test
    public void whenGetKeyHasKeysSccess() throws Exception {
        mvc.perform(get("/.well-known/jwks.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasKey("keys")));
    }

}