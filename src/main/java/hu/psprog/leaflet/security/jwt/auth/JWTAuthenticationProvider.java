package hu.psprog.leaflet.security.jwt.auth;

import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreValidationStatus;
import hu.psprog.leaflet.security.sessionstore.exception.SessionStoreValidationException;
import hu.psprog.leaflet.security.sessionstore.service.SessionStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Spring {@link AuthenticationProvider} implementation for JWT token based authentication.
 *
 * @author Peter Smith
 */
@Component
public class JWTAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SessionStoreService sessionStoreService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        JWTAuthenticationToken jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        SessionStoreValidationStatus status = sessionStoreService.validateToken(jwtAuthenticationToken);
        if (status != SessionStoreValidationStatus.VALID) {
            throw new SessionStoreValidationException(String.valueOf(authentication.getPrincipal()), status);
        }
        authentication.setAuthenticated(true);

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
