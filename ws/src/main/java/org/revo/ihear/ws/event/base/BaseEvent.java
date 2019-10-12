package org.revo.ihear.ws.event.base;

import org.springframework.context.ApplicationEvent;

abstract class BaseEvent extends ApplicationEvent {

    BaseEvent(Object source) {
        super(source);
    }
}
