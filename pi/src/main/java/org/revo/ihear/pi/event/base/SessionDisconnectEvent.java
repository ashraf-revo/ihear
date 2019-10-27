package org.revo.ihear.pi.event.base;

import org.springframework.web.reactive.socket.WebSocketSession;

public class SessionDisconnectEvent extends BaseEvent {
    private WebSocketSession session;

    public SessionDisconnectEvent(Object source, WebSocketSession session) {
        super(source);
        setSession(session);
    }

    public WebSocketSession getSession() {
        return session;
    }

    void setSession(WebSocketSession session) {
        this.session = session;
    }
}
