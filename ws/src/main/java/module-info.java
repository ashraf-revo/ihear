module ihear.ws {
    requires ihear.base;

    requires org.reactivestreams;

    requires spring.webflux;
    requires spring.web;
    requires spring.context;
    requires spring.beans;
    requires reactor.core;
    requires spring.cloud.commons;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.boot.actuator.autoconfigure;

    requires spring.data.mongodb;
    requires spring.data.commons;
    requires spring.security.config;
    requires spring.security.web;
    requires spring.security.oauth2.jose;
    requires spring.security.oauth2.resource.server;
    requires spring.security.core;

}
