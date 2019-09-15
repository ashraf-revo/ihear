package org.revo.ihear.ws.event.base;

import org.springframework.web.reactive.socket.WebSocketSession;

public class SessionConnectEvent extends BaseEvent {
    public SessionConnectEvent(Object source, WebSocketSession session) {
        super(source);
        setSession(session);
    }
}
