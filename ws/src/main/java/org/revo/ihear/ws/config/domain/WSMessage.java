package org.revo.ihear.ws.config.domain;

public class WSMessage {
    private String payload;
    private String from;
    private String to;

    public WSMessage() {
    }

    public String getPayload() {
        return payload;
    }

    public WSMessage setPayload(String payload) {
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
