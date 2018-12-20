package hu.psprog.leaflet.security.sessionstore.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Common configuration for Session Store.
 *
 * @author Peter Smith
 */
@Configuration
@Import(SessionStoreDataSourceConfiguration.class)
@EnableScheduling
@ComponentScan(basePackages = {
        "hu.psprog.leaflet.security.sessionstore.conversion",
        "hu.psprog.leaflet.security.sessionstore.dao.impl",
        "hu.psprog.leaflet.security.sessionstore.service.impl",
        "hu.psprog.leaflet.security.sessionstore.task"})
public class SessionStoreConfiguration {
}
