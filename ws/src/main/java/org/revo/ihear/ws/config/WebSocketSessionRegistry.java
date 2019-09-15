package org.revo.ihear.ws.config;

import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WebSocketSessionRegistry {
    private Map<String, List<WebSocketSession>> sessionByUserId = new ConcurrentHashMap<>();

    public void add(String key, WebSocketSession session) {
        sessionByUserId.computeIfPresent(key, (key1, sessions) -> {
            sessions.add(session);
            return sessions;
        });
        sessionByUserId.computeIfAbsent(key, s -> new ArrayList<>(Collections.singleton(session)));
    }

    public void remove(String key, WebSocketSession session) {
        List<WebSocketSession> result = sessionByUserId.computeIfPresent(key, (key1, sessions) -> sessions.stream().filter(it -> !it.getId().equals(session.getId())).collect(Collectors.toList()));
        if (result == null || result.size() == 0) sessionByUserId.remove(key);
    }

    public List<WebSocketSession> sessions(String userId) {
        return sessionByUserId.get(userId);
    }

    public Set<String> getAllActiveUsers() {
        return sessionByUserId.keySet();
    }
}
