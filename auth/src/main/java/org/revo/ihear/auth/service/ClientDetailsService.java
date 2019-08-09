package org.revo.ihear.auth.service;

import org.revo.base.domain.ClientDetails;

import java.util.Optional;

public interface ClientDetailsService {
    Optional<ClientDetails> findByClientId(String ClientId);

    void save(ClientDetails clientDetails);

    long count();
}
