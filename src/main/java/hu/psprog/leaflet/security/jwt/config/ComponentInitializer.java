package hu.psprog.leaflet.security.jwt.config;

import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Reads up initial configuration values for JWT component.
 *
 * @author Peter Smith
 */
@Configuration
public class ComponentInitializer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentInitializer.class);
    private static final String CONFIG_JWT_SECRET = "${jwt.secret}";
    private static final String CONFIG_JWT_EXPIRATION_IN_HOURS = "${jwt.expire.hours}";

    @Value(CONFIG_JWT_SECRET)
    private String jwtSecret;

    @Value(CONFIG_JWT_EXPIRATION_IN_HOURS)
    private Integer expirationInHours;

    @Bean
    public String jwtSecret() {
        return jwtSecret;
    }

    @Bean
    public Integer expirationInHours() {
        return expirationInHours;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(jwtSecret, "JWT Secret must be set!");
        Assert.notNull(expirationInHours, "JWT Expiration must be set!");
        LOGGER.info(String.format("Current JWT Secret value is [%s].", jwtSecret));
        LOGGER.info(String.format("JWT tokens will expire in %d hours", expirationInHours));
    }
}
