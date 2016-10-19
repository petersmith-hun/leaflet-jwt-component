package hu.psprog.leaflet.security.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception to be thrown when Authorization header field is missing or not containing a valid JWT token.
 *
 * @author Peter Smith
 */
public class InvalidAuthorizationHeaderException extends AuthenticationException {

    public InvalidAuthorizationHeaderException() {
        super("Invalid or missing Authorization header");
    }

    public InvalidAuthorizationHeaderException(String message) {
        super(message);
    }
}
