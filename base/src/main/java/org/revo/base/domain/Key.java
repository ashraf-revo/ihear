package org.revo.base.domain;

import java.util.ArrayList;
import java.util.List;

public class Key {
    private KeyType keyType;
    private List<Listener> listeners = new ArrayList<>();

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public List<Listener> getListeners() {
        return listeners;
    }

    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }
}
