package org.revo.ihear.auth.service.impl;

import org.revo.ihear.auth.repository.ClientDetailsRepository;
import org.revo.ihear.auth.service.ClientDetailsService;
import org.revo.ihear.entites.domain.ClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {
    @Autowired
    private ClientDetailsRepository clientDetailsRepository;

    @Override
    public Optional<ClientDetails> findByClientId(String clientId) {
        Optional<ClientDetails> byClientId = clientDetailsRepository.findByClientId(clientId);
       return byClientId.map(it->{
            it.setAutoApproveScopes(new HashSet<>());
            return it;
        });
//        return byClientId;
    }

    @Override
    public ClientDetails save(ClientDetails clientDetails) {
        return clientDetailsRepository.save(clientDetails);
    }

    @Override
    public long count() {
        return clientDetailsRepository.count();
    }
}
