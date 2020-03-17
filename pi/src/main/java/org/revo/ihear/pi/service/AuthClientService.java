package org.revo.ihear.pi.service;

import org.revo.ihear.entites.domain.BaseClientDetails;
import reactor.core.publisher.Mono;

public interface AuthClientService {
    Mono<BaseClientDetails> createClient(BaseClientDetails baseClientDetails);
}
