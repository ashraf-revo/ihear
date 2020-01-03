package org.revo.ihear.pi.domain;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private List<Resource> resources = new ArrayList<>();
    private List<Listener> listeners = new ArrayList<>();
    private List<Key> keys = new ArrayList<>();

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Listener> getListeners() {
        return listeners;
    }

    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }
}
