package hu.psprog.leaflet.security.jwt.filter;

import hu.psprog.leaflet.security.jwt.JWTComponent;
import hu.psprog.leaflet.security.jwt.auth.JWTAuthenticationToken;
import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.jwt.model.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static hu.psprog.leaflet.security.jwt.filter.JWTAuthenticationFilter.AUTH_TOKEN_HEADER;
import static hu.psprog.leaflet.security.jwt.filter.JWTAuthenticationFilter.DEVICE_ID_HEADER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Unit tests for {@link JWTAuthenticationFilter}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class JWTAuthenticationFilterTest {

    private static final String TOKEN = "token";
    private static final String USERNAME = "username";
    private static final UUID DEVICE_ID = UUID.randomUUID();
    private static final String REMOTE_ADDRESS = "remote-address";
    public static final String ANONYMOUS = "ANONYMOUS";

    @Mock
    private JWTComponent jwtComponent;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Before
    public void setup() {
        jwtAuthenticationFilter.setAuthenticationManager(authentication -> authentication);
    }

    @Test
    public void shouldAttemptAuthentication() throws IOException, ServletException {

        // given
        JWTPayload jwtPayload = JWTPayload.getBuilder()
                .withUsername(USERNAME)
                .withRole(Role.USER)
                .build();
        given(jwtComponent.extractToken(httpServletRequest)).willReturn(TOKEN);
        given(jwtComponent.decode(TOKEN)).willReturn(jwtPayload);
        given(httpServletRequest.getHeader(DEVICE_ID_HEADER)).willReturn(DEVICE_ID.toString());
        given(httpServletRequest.getRemoteAddr()).willReturn(REMOTE_ADDRESS);

        // when
        Authentication result = jwtAuthenticationFilter.attemptAuthentication(httpServletRequest, httpServletResponse);

        // then
        assertThat(result, notNullValue());
        assertThat(result instanceof JWTAuthenticationToken, is(true));
        assertThat(result.getAuthorities().size(), equalTo(1));
        assertThat(result.getAuthorities().contains(new SimpleGrantedAuthority("USER")), is(true));
        assertThat(result.getDetails(), equalTo(jwtPayload));
        assertThat(((JWTAuthenticationToken) result).getRawToken(), equalTo(TOKEN));
        assertThat(((JWTAuthenticationToken) result).getDeviceID(), equalTo(DEVICE_ID));
        assertThat(((JWTAuthenticationToken) result).getRemoteAddress(), equalTo(REMOTE_ADDRESS));
    }

    @Test
    public void shouldAttemptAuthenticationWithAnonymousUserIfNoTokenProvided() throws IOException, ServletException {

        // given

        // when
        Authentication result = jwtAuthenticationFilter.attemptAuthentication(httpServletRequest, httpServletResponse);

        // then
        assertAnonymousUser(result);
    }

    @Test
    public void shouldAttemptAuthenticationWithAnonymousUserProvidedTokenIsNotParsable() throws IOException, ServletException {

        // given
        doThrow(RuntimeException.class).when(jwtComponent).extractToken(httpServletRequest);

        // when
        Authentication result = jwtAuthenticationFilter.attemptAuthentication(httpServletRequest, httpServletResponse);

        // then
        assertAnonymousUser(result);
    }

    @Test
    public void shouldAddTokenToResponseHeader() throws IOException, ServletException {

        // given
        JWTPayload jwtPayload = JWTPayload.getBuilder()
                .withRole(Role.USER)
                .build();
        Authentication authentication = JWTAuthenticationToken.getBuilder()
                .withPayload(jwtPayload)
                .withRawToken(TOKEN)
                .build();

        // when
        jwtAuthenticationFilter.successfulAuthentication(httpServletRequest, httpServletResponse, filterChain, authentication);

        // then
        verify(httpServletResponse).setHeader(AUTH_TOKEN_HEADER, TOKEN);
    }

    @Test
    public void shouldNotAddTokenToResponseHeaderForReclaimUser() throws IOException, ServletException {

        // given
        JWTPayload jwtPayload = JWTPayload.getBuilder()
                .withRole(Role.RECLAIM)
                .build();
        Authentication authentication = JWTAuthenticationToken.getBuilder()
                .withPayload(jwtPayload)
                .withRawToken(TOKEN)
                .build();

        // when
        jwtAuthenticationFilter.successfulAuthentication(httpServletRequest, httpServletResponse, filterChain, authentication);

        // then
        verifyZeroInteractions(httpServletResponse);
    }

    @Test
    public void shouldNotAddTokenToResponseHeaderForAnonymousUser() throws IOException, ServletException {

        // given
        Authentication authentication = new AnonymousAuthenticationToken(ANONYMOUS, ANONYMOUS, AuthorityUtils.createAuthorityList(ANONYMOUS));

        // when
        jwtAuthenticationFilter.successfulAuthentication(httpServletRequest, httpServletResponse, filterChain, authentication);

        // then
        verifyZeroInteractions(httpServletResponse);
    }

    private void assertAnonymousUser(Authentication result) {
        assertThat(result, notNullValue());
        assertThat(result instanceof AnonymousAuthenticationToken, is(true));
        assertThat(result.getPrincipal(), equalTo(ANONYMOUS));
        assertThat(result.getAuthorities().size(), equalTo(1));
        assertThat(result.getAuthorities().contains(new SimpleGrantedAuthority(ANONYMOUS)), is(true));
    }
}
