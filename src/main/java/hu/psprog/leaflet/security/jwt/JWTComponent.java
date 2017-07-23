package hu.psprog.leaflet.security.jwt;

import hu.psprog.leaflet.security.jwt.exception.InvalidAuthorizationHeaderException;
import hu.psprog.leaflet.security.jwt.exception.InvalidJWTTokenException;
import hu.psprog.leaflet.security.jwt.model.JWTAuthenticationAnswerModel;
import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * JWT Component operations.
 *
 * @author Peter Smith
 */
public interface JWTComponent {

    /**
     * Generates token from {@link UserDetails} object.
     *
     * @param userDetails {@link UserDetails} object to generate token based on
     * @return token wrapped in {@link JWTAuthenticationAnswerModel} object
     */
    JWTAuthenticationAnswerModel generateToken(UserDetails userDetails);

    /**
     * Decodes given JWT token and returns its payload's content as {@link JWTPayload} object.
     *
     * @param token given (raw) token
     * @return {@link JWTPayload} object on success with the contents of JWT payload section
     * @throws InvalidJWTTokenException
     */
    JWTPayload decode(String token) throws InvalidJWTTokenException;

    /**
     * Extracts token from servlet request. Requires Bearer type Authorization header.
     * If Authorization and/or Bearer not found, {@link InvalidAuthorizationHeaderException} will be thrown.
     *
     * @param request standard {@link HttpServletRequest}
     * @return on success, extracted token will be returned as string
     * @throws InvalidAuthorizationHeaderException
     */
    String extractToken(HttpServletRequest request) throws InvalidAuthorizationHeaderException;
}
