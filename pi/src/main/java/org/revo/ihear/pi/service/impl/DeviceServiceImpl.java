package org.revo.ihear.pi.service.impl;

import org.revo.ihear.entites.domain.BaseClientDetails;
import org.revo.ihear.pi.domain.Device;
import org.revo.ihear.pi.repository.DeviceRepository;
import org.revo.ihear.pi.service.DeviceService;
import org.revo.ihear.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    @Qualifier("microWebClient")
    private WebClient microWebClient;
    @Autowired
    private AuthService authService;

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
                .map(it -> deviceRepository.save(it))
                .map(it -> it.setClientSecret(clientSecret));

    }

    @Override
    public Optional<Device> findOneById(String id) {
        return deviceRepository.findById(id);
    }

    @Override
    public List<Device> findAll(String id) {
        return deviceRepository.findAllByCreatedBy(id);
    }

    private Mono<BaseClientDetails> createClient(BaseClientDetails baseClientDetails) {
        return microWebClient.post().uri("http://localhost:9999/auth/client").body(Mono.just(baseClientDetails)
                , BaseClientDetails.class).exchange().flatMap(it -> it.bodyToMono(BaseClientDetails.class));
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
        return this.createClient(baseClientDetails);
    }
}
