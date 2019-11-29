package org.revo.ihear.pi.domain;

public class Listener {
    private ListenerType listenerType;
    private Action action;

    public ListenerType getListenerType() {
        return listenerType;
    }

    public void setListenerType(ListenerType listenerType) {
        this.listenerType = listenerType;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
