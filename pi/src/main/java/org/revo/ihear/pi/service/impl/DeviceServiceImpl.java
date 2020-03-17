package org.revo.ihear.pi.service.impl;

import org.revo.ihear.entites.domain.BaseClientDetails;
import org.revo.ihear.pi.domain.Device;
import org.revo.ihear.pi.repository.DeviceRepository;
import org.revo.ihear.pi.service.AuthClientService;
import org.revo.ihear.pi.service.DeviceService;
import org.revo.ihear.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthClientService authClientService;

    private static String random() {
        return (UUID.randomUUID().toString().hashCode() & 0xfffffff) + "";
    }

    public Mono<Device> save(Device device) {
        String clientSecret = random();
        return authService.currentJwtUserId().flatMap(it -> {
            if (device.getClientId() == null) {
                String clintId = random() + "@" + it;
                return this.createRandomClient(clintId, clientSecret, device.getCreatedBy())
                        .map(cd -> device.setClientId(cd.getClientId()));
            } else
                return Mono.just(device);
        })
                .flatMap(it -> deviceRepository.save(it))
                .map(it -> it.setClientSecret(clientSecret));

    }

    @Override
    public Mono<Device> findOneById(String id) {
        return deviceRepository.findById(id);
    }

    @Override
    public Flux<Device> findAll(String id) {
        return deviceRepository.findAllByCreatedBy(id);
    }


    private Mono<BaseClientDetails> createRandomClient(String clienId, String password, String createdFor) {
        BaseClientDetails baseClientDetails = new BaseClientDetails();
        baseClientDetails.setClientId(clienId);
        baseClientDetails.setClientSecret(password);
        baseClientDetails.setCreatedFor(createdFor);
        baseClientDetails.setScope(new HashSet<>(Arrays.asList("read", "write")));
        baseClientDetails.setAutoApproveScopes(new HashSet<>(Arrays.asList("read", "write")));
        baseClientDetails.setAccessTokenValiditySeconds(43200);
        baseClientDetails.setRefreshTokenValiditySeconds(43200);
        baseClientDetails.setAuthorizedGrantTypes(new HashSet<>(Collections.singletonList("client_credentials")));
        return authClientService.createClient(baseClientDetails);
    }
}
