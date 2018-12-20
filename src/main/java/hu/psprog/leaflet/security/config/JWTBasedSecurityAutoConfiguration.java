package hu.psprog.leaflet.security.config;

import hu.psprog.leaflet.security.jwt.config.JWTComponentInitializer;
import hu.psprog.leaflet.security.sessionstore.config.SessionStoreConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot auto-configuration for JWT component based security.
 *
 * @author Peter Smith
 */
@Configuration
@Import({JWTComponentInitializer.class,
        SessionStoreConfiguration.class})
public class JWTBasedSecurityAutoConfiguration {
}
