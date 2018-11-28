module leaflet.component.security.jwt {
    requires java.sql;
    requires java.validation;
    requires javax.servlet.api;
    requires h2;
    requires jackson.databind;
    requires jjwt;
    requires org.apache.commons.lang3;
    requires slf4j.api;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.core;
    requires spring.jdbc;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.tx;

    exports hu.psprog.leaflet.security.jwt;
    exports hu.psprog.leaflet.security.jwt.auth;
    exports hu.psprog.leaflet.security.jwt.exception;
    exports hu.psprog.leaflet.security.jwt.filter;
    exports hu.psprog.leaflet.security.jwt.model;
    exports hu.psprog.leaflet.security.sessionstore.domain;
    exports hu.psprog.leaflet.security.sessionstore.service;
}