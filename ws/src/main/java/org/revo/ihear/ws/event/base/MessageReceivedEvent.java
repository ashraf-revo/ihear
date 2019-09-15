package org.revo.ihear.ws.event.base;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

public class MessageReceivedEvent extends BaseEvent {
    private WebSocketMessage webSocketMessage;

    public MessageReceivedEvent(Object source, WebSocketSession session, WebSocketMessage webSocketMessage) {
        super(source);
        setSession(session);
        this.webSocketMessage = webSocketMessage;
    }

    public WebSocketMessage getWebSocketMessage() {
        return webSocketMessage;
    }

    public MessageReceivedEvent setWebSocketMessage(WebSocketMessage webSocketMessage) {
        this.webSocketMessage = webSocketMessage;
        return this;
    }
}
