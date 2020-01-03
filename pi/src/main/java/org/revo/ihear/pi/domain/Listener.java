package org.revo.ihear.pi.domain;

import java.util.List;

public class Listener {
    private ListenerType listenerType;
    private boolean threading;
    private List<Action> actions;

    public ListenerType getListenerType() {
        return listenerType;
    }

    public void setListenerType(ListenerType listenerType) {
        this.listenerType = listenerType;
    }

    public boolean isThreading() {
        return threading;
    }

    public void setThreading(boolean threading) {
        this.threading = threading;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
