package hu.psprog.leaflet.security.jwt;

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
     * Generates token from {@link UserDetails} object with default expiration length.
     *
     * @param userDetails {@link UserDetails} object to generate token based on
     * @return token wrapped in {@link JWTAuthenticationAnswerModel} object
     */
    JWTAuthenticationAnswerModel generateToken(UserDetails userDetails);

    /**
     * Generates token from {@link UserDetails} object with custom expiration length.
     *
     * @param userDetails {@link UserDetails} object to generate token based on
     * @param expiration expiration length in hours
     * @return token wrapped in {@link JWTAuthenticationAnswerModel} object
     */
    JWTAuthenticationAnswerModel generateToken(UserDetails userDetails, Integer expiration);

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
     *
     * @param request standard {@link HttpServletRequest}
     * @return on success, extracted token will be returned as string
     */
    String extractToken(HttpServletRequest request);
}
