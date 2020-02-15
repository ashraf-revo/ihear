package org.revo.ihear.pi.domain;

import java.util.HashMap;
import java.util.Map;

public class Action {
    private ActionType actionType;
    private ResourceType resourceType;
    private Map<String, String> data = new HashMap<>();

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
