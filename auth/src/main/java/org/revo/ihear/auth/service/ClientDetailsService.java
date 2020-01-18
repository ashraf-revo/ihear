package org.revo.ihear.auth.service;

import org.revo.ihear.entites.domain.ClientDetails;

import java.util.Optional;

public interface ClientDetailsService {
    Optional<ClientDetails> findByClientId(String ClientId);

    ClientDetails save(ClientDetails clientDetails);

    long count();
}
