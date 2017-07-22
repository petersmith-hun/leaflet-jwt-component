package hu.psprog.leaflet.security.sessionstore.exception;

import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreValidationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

/**
 * Exception to throw upon
 *
 * @author Peter Smith
 */
public class SessionStoreValidationException extends AuthenticationException {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionStoreValidationException.class);

    private static final String NOT_SIGNED_IN_MESSAGE = "Your are not signed in. Please sign in to proceed.";
    private static final String SESSION_STORE_VALIDATION = "Failed to validate token for user [%s] based on Session Store information. Token status is [%s].";

    public SessionStoreValidationException(String username, SessionStoreValidationStatus status) {
        super(NOT_SIGNED_IN_MESSAGE);
        LOGGER.error(String.format(SESSION_STORE_VALIDATION, username, status));
    }
}
