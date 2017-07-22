package hu.psprog.leaflet.security.sessionstore.dao;

import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;

import java.util.List;
import java.util.Optional;

/**
 * Session store database operations.
 *
 * @author Peter Smith
 */
public interface SessionStoreDAO {

    /**
     * Returns all existing tokens in session store.
     *
     * @return List of tokens as {@link SessionStoreTokenEntry} objects
     */
    List<SessionStoreTokenEntry> getAllTokenEntries();

    /**
     * Stores a new token in session store upon login.
     *
     * @param sessionStoreTokenEntry token and all required additional information as {@link SessionStoreTokenEntry} object
     */
    void insertTokenEntry(SessionStoreTokenEntry sessionStoreTokenEntry);

    /**
     * Retrieves a {@link SessionStoreTokenEntry} by provided token.
     *
     * @param token token to retrieve {@link SessionStoreTokenEntry} for
     * @return optional SessionStoreTokenEntry object
     */
    Optional<SessionStoreTokenEntry> getTokenEntry(String token);

    /**
     * Updates an existing token's status.
     *
     * @param token token to update status for
     * @param status new status
     */
    void updateTokenEntry(String token, TokenStatus status);

    /**
     * Removes a token from session store.
     *
     * @param token token to remove
     */
    void removeTokenEntry(String token);
}
