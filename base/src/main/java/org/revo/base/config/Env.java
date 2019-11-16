package org.revo.base.config;

import org.revo.base.domain.ClientDetails;
import org.revo.base.domain.User;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("org.revo.base.env")
public class Env {
    private List<User> users = new ArrayList<>();
    private List<ClientDetails> clientDetails = new ArrayList<>();
    private String url;
    private String streamUrl;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<ClientDetails> getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(List<ClientDetails> clientDetails) {
        this.clientDetails = clientDetails;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
}
