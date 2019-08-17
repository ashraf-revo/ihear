package org.revo.base.domain;

public class Action {
    public Action() {
    }

    public Action(String id, Status status, Integer port) {
        this.id = id;
        this.status = status;
        this.port = port;
    }

    public Action(Status status, Integer port) {
        this.status = status;
        this.port = port;
    }

    private String id;
    private Status status;
    private Integer port;

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getPort() {
        return port;
    }
}
