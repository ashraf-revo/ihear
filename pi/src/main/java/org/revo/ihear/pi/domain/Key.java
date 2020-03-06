package org.revo.ihear.pi.domain;

import java.util.ArrayList;
import java.util.List;

public class Key {
    private KeyType keyType;
    private KeyEvent keyEvent;
    private List<Action> actions = new ArrayList<>();

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    public void setKeyEvent(KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
