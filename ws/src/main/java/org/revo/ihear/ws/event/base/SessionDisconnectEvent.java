package org.revo.ihear.ws.event.base;

import org.springframework.web.reactive.socket.WebSocketSession;

public class SessionDisconnectEvent extends BaseEvent {
    public SessionDisconnectEvent(Object source, WebSocketSession session) {
        super(source);
        setSession(session);
    }
}
