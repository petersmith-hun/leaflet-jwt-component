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
}