package org.revo.ihear.ui.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RemoveSessionHeaderResponse extends AbstractGatewayFilterFactory<RemoveSessionHeaderResponse.Config> {
    public RemoveSessionHeaderResponse() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            String name = "Set-Cookie";
            String value = exchange.getResponse().getHeaders().getFirst(name);
            if (value != null) {
                exchange.getResponse().getHeaders().set(name, value.replaceAll("JSESSIONID=[0-9a-zA-Z]+; ", ""));
                if (value.contains("SESSION=;")) exchange.getResponse().getHeaders().remove(name);
            }
        }));
    }

    static class Config {
    }
}
