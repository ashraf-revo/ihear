package org.revo.ihear.ws.config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WebSocketSessionRegistry {
    private Map<String, List<String>> sessionByUserId = new ConcurrentHashMap<>();
    private Map<String, String> userIdBySessionId = new ConcurrentHashMap<>();

    public void add(String userId, String sessionId) {
        sessionByUserId.computeIfPresent(userId, (key1, sessions) -> {
            sessions.add(sessionId);
            return sessions;
        });
        sessionByUserId.computeIfAbsent(userId, s -> new ArrayList<>(Collections.singleton(sessionId)));
        userIdBySessionId.computeIfAbsent(sessionId, s -> userId);
    }

    private void remove(String userId, String session) {
        List<String> result = sessionByUserId.computeIfPresent(userId, (key1, sessions) -> sessions.stream().filter(it -> !it.equals(session)).collect(Collectors.toList()));
        if (result == null || result.size() == 0) sessionByUserId.remove(userId);
        userIdBySessionId.remove(session);
    }

    public void remove(String sessionId) {
        if (userIdBySessionId.containsKey(sessionId)) remove(userIdBySessionId.get(sessionId), sessionId);
    }

    public List<String> sessions(String userId) {
        if (!sessionByUserId.containsKey(userId))
            return Collections.emptyList();
        return sessionByUserId.get(userId);
    }

    public Set<String> getAllActiveUsers() {
        return sessionByUserId.keySet();
    }
}
