package hu.psprog.leaflet.security.jwt.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Spring {@link AuthenticationProvider} implementation for JWT token based authentication.
 *
 * @author Peter Smith
 */
public class JWTAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        authentication.setAuthenticated(true);

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
