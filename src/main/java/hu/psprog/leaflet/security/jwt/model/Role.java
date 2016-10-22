package hu.psprog.leaflet.security.jwt.model;

/**
 * User roles.
 *
 * @author Peter Smith
 */
public enum Role {

    /**
     * For ANONYMOUS authentication - automatically assigned to anonymous user.
     * Only public endpoints shall be accessed with ANONYMOUS role.
     */
    ANONYMOUS,

    /**
     * Default visitor role. Highly restricted endpoint access shall be assigned.
     */
    USER,

    /**
     * Editor users. They can access blog management sections on administrator application.
     */
    EDITOR,

    /**
     * System administrator role - full control of the system.
     */
    ADMIN,

    /**
     * Virtual service users can access special endpoints. Ex.: CBFS can communicate with Leaflet through SERVICE token.
     */
    SERVICE
}
