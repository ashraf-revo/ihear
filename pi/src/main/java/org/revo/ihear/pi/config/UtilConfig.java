package org.revo.ihear.pi.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class UtilConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder builder(ReactiveClientRegistrationRepository clientRegistrationRepository
            , ServerOAuth2AuthorizedClientRepository serverOAuth2AuthorizedClientRepository) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrationRepository, serverOAuth2AuthorizedClientRepository/*new UnAuthenticatedServerOAuth2AuthorizedClientRepository()*/);
        oauth.setDefaultClientRegistrationId("micro");
        return WebClient.builder().filter(oauth);
    }
    @Bean
    public WebClient microWebClient(WebClient.Builder builder){
        return builder.build();
    }

}
