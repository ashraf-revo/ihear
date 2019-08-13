module ihear.base {
    exports org.revo.base.config;
    exports org.revo.base.domain;
    exports org.revo.base.service;
    requires spring.security.oauth2;


    requires spring.security.oauth2.jose;
    requires org.reactivestreams;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    requires reactor.core;
    requires spring.security.core;
    requires spring.boot;
    requires spring.data.mongodb;
    requires spring.data.commons;
    requires spring.context;
    requires spring.beans;
    requires spring.boot.autoconfigure;

}