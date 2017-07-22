package hu.psprog.leaflet.security.jwt.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

/**
 * Exception to be thrown when JWT token has invalid structure or some security constraints are violated (token expires, invalid signature, etc.).
 *
 * @author Peter Smith
 */
public class InvalidJWTTokenException extends AuthenticationException {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidJWTTokenException.class);

    private static final String NOT_SIGNED_IN_MESSAGE = "Your are not signed in. Please sign in to proceed.";
    private static final String EXCEPTION_MESSAGE = "Found issues with provided JWT token while parsing.";

    public InvalidJWTTokenException(Exception exception) {
        super(NOT_SIGNED_IN_MESSAGE, exception);
        LOGGER.error(EXCEPTION_MESSAGE, exception);
    }
}
