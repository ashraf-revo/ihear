package org.revo.ihear.auth.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.revo.ihear.entites.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataMongoTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void whenLoadExpectDefaltUsers() {
        int expectedSize = 1;
        assertThat(userService.findAll(), hasSize(expectedSize));
    }

    @Test
    public void whenFetchRevoDefaultUserExpectSuccess() {
        String expected = "revo";
        Optional<User> revo = userService.findByUsername(expected);
        assertThat(revo.isPresent(), is(true));
        Assertions.assertDoesNotThrow(() -> revo.get());
        assertThat(revo.get(), hasProperty("username", is(expected)));
    }

    @Test
    public void whenFetchRevoDefaultUserHaveAuthoritiesExpectSuccess() {
        String expected = "revo";
        Optional<User> revo = userService.findByUsername(expected);
        assertThat(revo.isPresent(), is(true));
        Assertions.assertDoesNotThrow(() -> revo.get());
        assertThat(revo.get(), hasProperty("enabled", is(true)));
        assertThat(revo.get(), hasProperty("authorities", hasSize(equalTo(1))));
        assertThat(revo.get(), anyOf(hasProperty("authorities"), is("ROLE_ADMIN")));
    }

    @Test
    public void whenFetchAnotherUserExpectSuccess() {
        String expected = "another";
        Optional<User> revo = userService.findByUsername(expected);
        assertThat(revo.isPresent(), is(false));
    }

    @Test
    public void whenSaveSuccessExpectSuccess() {
        long expectedCount = userService.count();
        String expected = "another";
        User user = new User();
        user.setUsername(expected);
        userService.save(user);
        assertThat(expectedCount + 1, is(userService.count()));
    }

    @Test
    public void whenSaveExistedUserExpectFail() {
        long expectedCount = userService.count();
        String expected = "revo";
        User user = new User();
        user.setUsername(expected);
        Assertions.assertThrows(DuplicateKeyException.class, () -> userService.save(user));
        assertThat(expectedCount, is(userService.count()));
    }

}