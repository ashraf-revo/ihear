package org.revo.ihear.auth.service.impl;

import org.revo.base.domain.User;
import org.revo.ihear.auth.domain.Key;
import org.revo.ihear.auth.repository.KeyRepository;
import org.revo.ihear.auth.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class KeyServiceImpl implements KeyService {
    @Autowired
    private KeyRepository keyRepository;
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Override
    public void delete(String id) {
        keyRepository.deleteByIdAndCreatedBy(id, currentUser().getId());
    }

    @Override
    public Key save(Key key) {
        key.setCreatedBy(currentUser().getId());
        return keyRepository.save(key);
    }

    @Override
    public List<Key> findAll() {
        return keyRepository.findAllByCreatedBy(currentUser().getId());
    }

    @Override
    public boolean exist(String id) {
        return keyRepository.existsById(id);
    }

    public OAuth2AccessToken generateDeviceJwtToken() {
        Key save = save(new Key());
        DefaultOAuth2RefreshToken refreshToken = new DefaultOAuth2RefreshToken(save.getId());
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(save.getId());
        accessToken.setRefreshToken(refreshToken);
        accessToken.setScope(new HashSet<>(Arrays.asList("ws", "rtsp", "read")));
        User user = currentUser();
        user.setRoles("ROLE_DEVICE");
        UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken(user, null);
        OAuth2Request fakeDevice = new OAuth2Request(null, "device", null, true, null, null, "", null, null);
        return jwtAccessTokenConverter.enhance(accessToken, new OAuth2Authentication(fakeDevice, userAuthentication));
    }

    private User currentUser() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
