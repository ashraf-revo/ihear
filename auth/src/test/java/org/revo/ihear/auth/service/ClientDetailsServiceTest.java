package org.revo.ihear.auth.service;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.revo.ihear.entites.domain.ClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataMongoTest
public class ClientDetailsServiceTest {
    @Autowired
    private ClientDetailsService clientDetailsService;

    @Test
    public void whenLoadExpectDefaltClients() {
        int expectedSize = 2;
        MatcherAssert.assertThat(clientDetailsService.findAll(), hasSize(expectedSize));
    }

    @Test
    public void whenFetchRevoDefaultClientExpectSuccess() {
        String expected = "revo";
        Optional<ClientDetails> revo = clientDetailsService.findByClientId(expected);
        assertThat(revo.isPresent(), is(true));
        Assertions.assertDoesNotThrow(() -> revo.get());
        assertThat(revo.get(), hasProperty("clientId", is(expected)));
    }

    @Test
    public void whenFetchMicroDefaultClientExpectSuccess() {
        String expected = "micro";
        Optional<ClientDetails> revo = clientDetailsService.findByClientId(expected);
        assertThat(revo.isPresent(), is(true));
        Assertions.assertDoesNotThrow(() -> revo.get());
        assertThat(revo.get(), hasProperty("clientId", is(expected)));
    }

}