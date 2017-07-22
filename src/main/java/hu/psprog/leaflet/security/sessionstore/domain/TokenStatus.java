package hu.psprog.leaflet.security.sessionstore.domain;

/**
 * Possible token statuses.
 *
 * @author Peter Smith
 */
public enum TokenStatus {

    /**
     * Token is active and valid.
     */
    ACTIVE,

    /**
     * Token has been revoked during it's active lifecycle.
     * Basically user has signed out.
     */
    REVOKED,

    /**
     * Token itself is valid, but the stored device ID and/or IP address is different.
     * In this scenario, an active token is used from two or more different locations.
     */
    COMPROMISED
}
