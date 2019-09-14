package org.revo.ihear.ui.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoveSessionHeaderRequest extends AbstractGatewayFilterFactory<RemoveSessionHeaderRequest.Config> {
    public RemoveSessionHeaderRequest() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange.mutate().request(exchange.getRequest().mutate()
                .headers(httpHeaders -> httpHeaders.remove("Cookie"))
                .build()).build());
    }

    static class Config {
    }
}
