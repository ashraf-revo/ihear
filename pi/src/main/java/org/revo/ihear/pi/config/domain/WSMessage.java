package org.revo.ihear.pi.config.domain;

public class WSMessage {
    private Object payload;
    private String from;
    private String to;

    public WSMessage() {
    }

    public Object getPayload() {
        return payload;
    }

    public WSMessage setPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    public String getTo() {
        return to;
    }

    public WSMessage setTo(String to) {
        this.to = to;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public WSMessage setFrom(String from) {
        this.from = from;
        return this;
    }

    @Override
    public String toString() {
        return "WSMessage{" +
                "payload='" + payload + '\'' +
                ", to='" + to + '\'' +
                ", from='" + from + '\'' +
                '}';
    }
}
