package org.revo.ihear.ui.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;

@Component
public class AddAuthorizationHeaderRequest extends AbstractGatewayFilterFactory<AddAuthorizationHeaderRequest.Config> {
    public AddAuthorizationHeaderRequest() {
        super(Config.class);
    }

    @Autowired
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> exchange.getPrincipal()
                .cast(OAuth2AuthenticationToken.class)
                .flatMap(authentication -> authorizedClientRepository.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication, exchange).cast(OAuth2AuthorizedClient.class))
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(token -> exchange.mutate().request(r -> r.headers(headers -> headers.setBearerAuth(token.getTokenValue()))).build())
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    static class Config {
    }
}
