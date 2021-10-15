package hu.psprog.leaflet.security.jwt.auth;

import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.jwt.model.Role;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreValidationStatus;
import hu.psprog.leaflet.security.sessionstore.exception.SessionStoreValidationException;
import hu.psprog.leaflet.security.sessionstore.service.SessionStoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link JWTAuthenticationProvider}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class JWTAuthenticationProviderTest {

    @Mock
    private SessionStoreService sessionStoreService;

    @InjectMocks
    private JWTAuthenticationProvider jwtAuthenticationProvider;

    private JWTAuthenticationToken jwtAuthenticationToken;

    @BeforeEach
    public void setup() {
        JWTPayload jwtPayload = JWTPayload.getBuilder()
                .withRole(Role.USER)
                .withUsername("username")
                .build();
        jwtAuthenticationToken = JWTAuthenticationToken.getBuilder()
                .withPayload(jwtPayload)
                .build();
    }

    @Test
    public void shouldAuthenticateSuccessfully() {

        // given
        given(sessionStoreService.validateToken(jwtAuthenticationToken)).willReturn(SessionStoreValidationStatus.VALID);

        // when
        Authentication result = jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);

        // then
        assertThat(result.isAuthenticated(), is(true));
    }

    @Test
    public void shouldAuthenticateWithFailure() {

        // given
        given(sessionStoreService.validateToken(jwtAuthenticationToken)).willReturn(SessionStoreValidationStatus.UNKNOWN_TOKEN);

        // when
        Assertions.assertThrows(SessionStoreValidationException.class, () -> jwtAuthenticationProvider.authenticate(jwtAuthenticationToken));

        // then
        // expected exception
    }
}
