module ihear.pi {
    requires ihear.base;

    requires org.reactivestreams;

    requires spring.webflux;
    requires spring.context;
    requires spring.beans;
    requires reactor.core;
    requires spring.cloud.commons;
    requires spring.boot.autoconfigure;
    requires spring.boot;

    requires spring.data.mongodb;
    requires spring.security.config;
    requires spring.security.web;
    requires spring.security.oauth2.jose;
    requires spring.security.oauth2.resource.server;
    requires spring.security.core;

requires spring.messaging;
    requires spring.amqp;
    requires spring.rabbit;
    requires spring.cloud.stream;


}