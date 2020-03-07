package org.revo.ihear.auth.controller;

import org.junit.jupiter.api.Test;
import org.revo.ihear.auth.service.ClientDetailsService;
import org.revo.ihear.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private ClientDetailsService clientDetailsService;
    @MockBean
    private UserService userService;

    @Test
    public void redirectToSiginInNotSigin() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isFound());
    }

    @Test
    public void renderSignupWhenRouteSignupSuccess() throws Exception {
        mvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void unauthorizeWhenRouteUserFail() throws Exception {
        mvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }
}