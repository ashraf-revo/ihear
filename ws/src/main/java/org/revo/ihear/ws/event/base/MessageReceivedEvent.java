package org.revo.ihear.ws.event.base;

import org.revo.ihear.ws.config.domain.WSMessage;
import org.springframework.messaging.Message;

public class MessageReceivedEvent extends BaseEvent {
    private Message<WSMessage> wsMessage;

    public MessageReceivedEvent(Object source, Message<WSMessage> wsMessage) {
        super(source);
        this.wsMessage = wsMessage;
    }

    public Message<WSMessage> getWsMessage() {
        return wsMessage;
    }

    public MessageReceivedEvent setWsMessage(Message<WSMessage> wsMessage) {
        this.wsMessage = wsMessage;
        return this;
    }
}
