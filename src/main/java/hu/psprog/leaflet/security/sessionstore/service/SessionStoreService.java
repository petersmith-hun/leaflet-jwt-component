package hu.psprog.leaflet.security.sessionstore.service;

import hu.psprog.leaflet.security.jwt.auth.JWTAuthenticationToken;
import hu.psprog.leaflet.security.sessionstore.domain.ClaimedTokenContext;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreValidationStatus;

/**
 * Session Store is an in-memory storage that stores all active tokens.
 * This service provides tools to post-validate and handle tokens.
 *
 * @author Peter Smith
 */
public interface SessionStoreService {

    /**
     * Stores given token in Session Store.
     *
     * @param claimedTokenContext {@link ClaimedTokenContext} object holding login information
     */
    void storeToken(ClaimedTokenContext claimedTokenContext);

    /**
     * Validates given token.
     * A token is considered valid if the token can be opened (non-expired and signature is valid - these checks should be already done by
     * the time this service is called) and post-validation is successful. Post-validation is considered successful, if the token is stored
     * in the Session Store and it's status is "ACTIVE".
     *
     * @param jwtAuthenticationToken {@link JWTAuthenticationToken} object from security context
     * @return validation status as {@link SessionStoreValidationStatus}
     */
    SessionStoreValidationStatus validateToken(JWTAuthenticationToken jwtAuthenticationToken);

    /**
     * Revokes given token (for example on sign-out).
     *
     * @param jwtAuthenticationToken {@link JWTAuthenticationToken} object from security context
     */
    void revokeToken(JWTAuthenticationToken jwtAuthenticationToken);

    /**
     * Removes expired tokens from Session Store.
     * Called automatically by scheduled cleanup task.
     * Tokens that are expired earlier then the current time minus the threshold (in minutes) will be removed.
     *
     * @param threshold threshold in minutes
     */
    void cleanExpiredToken(int threshold);
}
