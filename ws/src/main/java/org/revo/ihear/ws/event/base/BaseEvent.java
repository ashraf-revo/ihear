package org.revo.ihear.ws.event.base;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.reactive.socket.WebSocketSession;

public abstract class BaseEvent extends ApplicationEvent {
    private WebSocketSession session;

    BaseEvent(Object source) {
        super(source);
    }

    public WebSocketSession getSession() {
        return session;
    }

    void setSession(WebSocketSession session) {
        this.session = session;
    }
}
