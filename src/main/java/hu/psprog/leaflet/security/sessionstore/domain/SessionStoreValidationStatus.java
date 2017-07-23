package hu.psprog.leaflet.security.sessionstore.domain;

/**
 * Validation statuses.
 *
 * @author Peter Smith
 */
public enum SessionStoreValidationStatus {

    /**
     * Token is validated successfully.
     */
    VALID,

    /**
     * Stored token origin (IP address and/or device ID) is different than the currently received.
     */
    DIFFERENT_SOURCE,

    /**
     * Received token is revoked or compromised.
     */
    INVALIDATED,

    /**
     * No information is available about the received token in Session Store.
     */
    UNKNOWN_TOKEN
}
