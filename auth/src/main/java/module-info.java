module ihear.auth {
    requires ihear.base;

    requires spring.web;
    requires spring.boot.starter.web;
    requires spring.webmvc;

    requires spring.security.config;
    requires spring.security.core;

    requires spring.security.oauth2;

    requires nimbus.jose.jwt;

    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.actuator;
    requires spring.boot.configuration.processor;

    requires spring.data.mongodb;
    requires spring.data.commons;


    opens org.revo.ihear.auth to spring.core;
    exports org.revo.ihear.auth to spring.beans, spring.context;
}