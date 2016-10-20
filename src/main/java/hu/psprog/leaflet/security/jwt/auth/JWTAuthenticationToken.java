package hu.psprog.leaflet.security.jwt.auth;

import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.jwt.impl.JWTComponentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * Spring {@link Authentication} implementation for JWT token based authentication.
 *
 * @author Peter Smith
 */
@Component
@Scope("SCOPE_REQUEST")
public class JWTAuthenticationToken implements Authentication {

    private static final String JWT_AUTH_NAME = "JWT Authentication";

    @Autowired
    private JWTComponentImpl jwtComponentImpl;

    private JWTPayload payload;
    private boolean authenticated = false;

    public JWTAuthenticationToken() {
        // Serializable
    }

    public JWTAuthenticationToken(String token) {
        this.payload = jwtComponentImpl.decode(token);
    }

    @Override
    public String getName() {
        return JWT_AUTH_NAME;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(payload.getRole().toString());

        return Arrays.asList(authority);
    }

    @Override
    public Object getCredentials() {
        return "";
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
}
