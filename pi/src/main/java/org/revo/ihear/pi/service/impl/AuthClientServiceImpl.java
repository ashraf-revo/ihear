package org.revo.ihear.pi.service.impl;

import org.revo.base.service.UtilService;
import org.revo.ihear.entites.domain.BaseClientDetails;
import org.revo.ihear.pi.service.AuthClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthClientServiceImpl implements AuthClientService {
    @Autowired
    private WebClient microWebClient;
    @Autowired
    private UtilService utilService;

    @Override
    public Mono<BaseClientDetails> createClient(BaseClientDetails baseClientDetails) {
        return microWebClient.post().uri(utilService.getBaseUrl() + "/auth/client").body(Mono.just(baseClientDetails)
                , BaseClientDetails.class).exchange().flatMap(it -> it.bodyToMono(BaseClientDetails.class));
    }
}
