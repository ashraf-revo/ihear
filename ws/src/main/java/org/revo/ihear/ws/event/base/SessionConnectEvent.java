package org.revo.ihear.ws.event.base;

import org.springframework.web.reactive.socket.WebSocketSession;

public class SessionConnectEvent extends BaseEvent {
    private WebSocketSession session;

    public SessionConnectEvent(Object source, WebSocketSession session) {
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
