package org.revo.ihear.auth.repository;

import org.revo.ihear.entites.domain.ClientDetails;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientDetailsRepository extends CrudRepository<ClientDetails, String> {
    Optional<ClientDetails> findByClientId(String clientId);
}
