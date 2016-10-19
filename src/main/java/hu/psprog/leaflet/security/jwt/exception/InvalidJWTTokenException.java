package hu.psprog.leaflet.security.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception to be thrown when JWT token has invalid structure or some security constraints are violated (token expires, invalid signature, etc.).
 *
 * @author Peter Smith
 */
public class InvalidJWTTokenException extends AuthenticationException {

    private static final String EXCEPTION_MESSAGE = "Found issues with provided JWT token while parsing.";

    public InvalidJWTTokenException(Exception exception) {
        super(EXCEPTION_MESSAGE, exception);
    }
}
