package org.revo.ihear.ui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
public class UiApplicationTests {
    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void contextLoads() throws InterruptedException {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrationRepository, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
        oauth.setDefaultClientRegistrationId("micro");
        WebClient client = WebClient.builder().filter(oauth).build();
        String url = "http://localhost:9999/auth/user";


        client.get().uri(url).retrieve()
                .bodyToMono(String.class)
                .subscribe(it -> {
                    System.out.println(it);
                }, error -> {
                    System.out.println(error.getMessage());
                });
        Thread.sleep(2000);
    }

}
