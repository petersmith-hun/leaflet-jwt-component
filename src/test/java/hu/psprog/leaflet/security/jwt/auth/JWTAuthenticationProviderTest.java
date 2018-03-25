package hu.psprog.leaflet.security.jwt.auth;

import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.jwt.model.Role;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreValidationStatus;
import hu.psprog.leaflet.security.sessionstore.exception.SessionStoreValidationException;
import hu.psprog.leaflet.security.sessionstore.service.SessionStoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link JWTAuthenticationProvider}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class JWTAuthenticationProviderTest {

    @Mock
    private SessionStoreService sessionStoreService;

    @InjectMocks
    private JWTAuthenticationProvider jwtAuthenticationProvider;

    private JWTAuthenticationToken jwtAuthenticationToken;

    @Before
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

    @Test(expected = SessionStoreValidationException.class)
    public void shouldAuthenticateWithFailure() {

        // given
        given(sessionStoreService.validateToken(jwtAuthenticationToken)).willReturn(SessionStoreValidationStatus.UNKNOWN_TOKEN);

        // when
        jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);

        // then
        // expected exception
    }
}
