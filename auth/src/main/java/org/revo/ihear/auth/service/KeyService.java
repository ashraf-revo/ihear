package org.revo.ihear.auth.service;

import org.revo.ihear.auth.domain.Key;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.List;

public interface KeyService {
    void delete(String id);

    Key save(Key key);

    List<Key> findAll();

    boolean exist(String id);

    OAuth2AccessToken generateDeviceJwtToken();
}
