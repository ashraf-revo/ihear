module ihear.ui {
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.boot.actuator;
    requires spring.security.config;
    requires spring.security.web;
    requires spring.webflux;
    requires spring.core;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.security.core;
    requires reactor.core;
    requires org.reactivestreams;


    requires spring.cloud.gateway.core;

    requires spring.security.oauth2.client;

    requires spring.security.oauth2.core;

    opens org.revo.ihear.ui to spring.core;
    exports org.revo.ihear.ui to spring.beans, spring.context;


}