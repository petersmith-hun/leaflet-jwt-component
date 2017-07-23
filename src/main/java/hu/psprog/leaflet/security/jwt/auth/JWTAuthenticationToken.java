package hu.psprog.leaflet.security.jwt.auth;

import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;
import java.util.UUID;

/**
 * Spring {@link Authentication} implementation for JWT token based authentication.
 *
 * @author Peter Smith
 */
public class JWTAuthenticationToken implements Authentication {

    private static final String JWT_AUTH_NAME = "JWT Authentication";

    private JWTPayload payload;
    private UUID deviceID;
    private String remoteAddress;
    private String rawToken;
    private boolean authenticated = false;
    private Collection<GrantedAuthority> authorities;

    private JWTAuthenticationToken() {
        // prevent instantiation
    }

    @Override
    public String getName() {
        return JWT_AUTH_NAME;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return rawToken;
    }

    @Override
    public Object getDetails() {
        return payload;
    }

    @Override
    public Object getPrincipal() {
        return payload.getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    public UUID getDeviceID() {
        return deviceID;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getRawToken() {
        return rawToken;
    }

    public static JWTAuthenticationTokenBuilder getBuilder() {
        return new JWTAuthenticationTokenBuilder();
    }

    /**
     * Builder for {@link JWTAuthenticationToken}.
     */
    public static final class JWTAuthenticationTokenBuilder {
        private JWTPayload payload;
        private UUID deviceID;
        private String remoteAddress;
        private String rawToken;

        private JWTAuthenticationTokenBuilder() {
        }

        public JWTAuthenticationTokenBuilder withPayload(JWTPayload payload) {
            this.payload = payload;
            return this;
        }

        public JWTAuthenticationTokenBuilder withDeviceID(UUID deviceID) {
            this.deviceID = deviceID;
            return this;
        }

        public JWTAuthenticationTokenBuilder withRemoteAddress(String remoteAddress) {
            this.remoteAddress = remoteAddress;
            return this;
        }

        public JWTAuthenticationTokenBuilder withRawToken(String rawToken) {
            this.rawToken = rawToken;
            return this;
        }

        public JWTAuthenticationToken build() {
            JWTAuthenticationToken jWTAuthenticationToken = new JWTAuthenticationToken();
            jWTAuthenticationToken.payload = this.payload;
            jWTAuthenticationToken.deviceID = this.deviceID;
            jWTAuthenticationToken.remoteAddress = this.remoteAddress;
            jWTAuthenticationToken.rawToken = this.rawToken;
            jWTAuthenticationToken.authorities = AuthorityUtils.createAuthorityList(payload.getRole().toString());
            return jWTAuthenticationToken;
        }
    }
}
