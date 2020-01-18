package org.revo.ihear.pi.config.domain;

import java.io.Serializable;
import java.util.Map;

public class WSMessage implements Serializable {
    private Map<String, Object> payload;
    private String from;
    private String to;

    public WSMessage() {
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public WSMessage setPayload(Map<String, Object> payload) {
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
