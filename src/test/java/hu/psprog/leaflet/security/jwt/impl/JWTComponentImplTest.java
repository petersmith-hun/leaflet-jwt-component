package hu.psprog.leaflet.security.jwt.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.psprog.leaflet.security.jwt.JWTComponent;
import hu.psprog.leaflet.security.jwt.exception.InvalidAuthorizationHeaderException;
import hu.psprog.leaflet.security.jwt.exception.InvalidJWTTokenException;
import hu.psprog.leaflet.security.jwt.model.ExtendedUserDetails;
import hu.psprog.leaflet.security.jwt.model.JWTAuthenticationAnswerModel;
import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.jwt.model.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link JWTComponentImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class JWTComponentImplTest {

    private static final String USERNAME = "username";
    private static final List<GrantedAuthority> AUTHORITY_LIST = AuthorityUtils.createAuthorityList("USER");
    private static final String NAME = "User Name";
    private static final long USER_ID = 123L;

    private static final long EXPIRATION_IN_HOURS = 4L;
    private static final String JWT_SECRET = "czNjcjN0";
    private static final String AUTHORIZATION = "Authorization";

    private JWTComponent jwtComponent;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private HttpServletRequest httpServletRequest;

    @Before
    public void setup() {
        jwtComponent = new JWTComponentImpl(JWT_SECRET, (int) EXPIRATION_IN_HOURS);
    }

    @Test
    public void shouldGenerateToken() throws IOException {
        
        // given
        ExtendedUserDetails userDetails = new ExtendedUserDetails.Builder()
                .withUsername(USERNAME)
                .withAuthorities(AUTHORITY_LIST)
                .withName(NAME)
                .withID(USER_ID)
                .build();
        
        // when
        JWTAuthenticationAnswerModel result = jwtComponent.generateToken(userDetails);
        
        // then
        Map<String, String> jwtPayload = extractJWTPayload(result.getToken());
        assertExpiration(jwtPayload);
        assertUserInfo(jwtPayload);
    }

    @Test
    public void shouldDecodeToken() {

        // given
        String token =  "eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEyMywid" +
                        "XNyIjoidXNlcm5hbWUiLCJuYW1lIjoiVXNlciB" +
                        "OYW1lIiwiZXhwIjoxNTAwNzUwNTA2LCJpYXQiO" +
                        "jE1MDA3MzYxMDYsInJvbCI6IlVTRVIifQ.OdxZ" +
                        "NNvX24LU1QOJlmhLLsWyI6BjTsVGDZP4yxGsOC" +
                        "jepx44yaewOV7JPXJajx67KEWHXIeGxvl-FgRsZBQbRw";

        // when
        JWTPayload result = jwtComponent.decode(token);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getUsername(), equalTo(USERNAME));
        assertThat(result.getRole(), equalTo(Role.USER));
        assertThat(result.getName(), equalTo(NAME));
        assertThat(result.getId(), equalTo((int) USER_ID));
        assertThat(result.getExpires().getTime(), equalTo(1500750506000L));
        assertThat(result.getIssuedAt().getTime(), equalTo(1500736106000L));
    }

    @Test(expected = InvalidJWTTokenException.class)
    public void shouldThrowInvalidJWTTokenExceptionOnInvalidToken() {

        // given
        String token = "invalid-token";

        // when
        jwtComponent.decode(token);

        // then
        // expected exception
    }

    @Test
    public void shouldExtractToken() {

        // given
        given(httpServletRequest.getHeader(AUTHORIZATION)).willReturn("Bearer token");

        // when
        String result = jwtComponent.extractToken(httpServletRequest);

        // then
        assertThat(result, notNullValue());
        assertThat(result, equalTo("token"));
    }

    @Test
    public void shouldReturnNullOnExtractTokenIfAuthorizationHeaderIsNotProvided() {

        // given
        given(httpServletRequest.getHeader(AUTHORIZATION)).willReturn(null);

        // when
        String result = jwtComponent.extractToken(httpServletRequest);

        // then
        assertThat(result, nullValue());
    }

    @Test(expected = InvalidAuthorizationHeaderException.class)
    public void shouldThrowExceptionOnExtractTokenIfAuthorizationHeaderIsInvalid() {

        // given
        given(httpServletRequest.getHeader(AUTHORIZATION)).willReturn("Something token");

        // when
        String result = jwtComponent.extractToken(httpServletRequest);

        // then
        // expected exception
    }

    private Map<String, String> extractJWTPayload(String token) throws IOException {

        String[] parts = token.split("\\.");
        String rawPayload = new String(Base64.getDecoder().decode(parts[1]));

        return objectMapper.readValue(rawPayload, new TypeReference<Map<String, String>>() {});
    }

    private void assertUserInfo(Map<String, String> jwtPayload) {
        assertThat(jwtPayload.get("usr"), equalTo(USERNAME));
        assertThat(jwtPayload.get("rol"), equalTo(AUTHORITY_LIST.get(0).getAuthority()));
        assertThat(jwtPayload.get("name"), equalTo(NAME));
        assertThat(jwtPayload.get("uid"), equalTo(String.valueOf(USER_ID)));
    }

    private void assertExpiration(Map<String, String> jwtPayload) {
        long difference = Long.parseLong(jwtPayload.get("exp")) - Long.parseLong(jwtPayload.get("iat"));
        assertThat(TimeUnit.SECONDS.toHours(difference), equalTo(EXPIRATION_IN_HOURS));
    }
}
