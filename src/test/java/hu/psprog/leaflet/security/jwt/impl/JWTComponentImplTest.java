package hu.psprog.leaflet.security.jwt.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.psprog.leaflet.security.jwt.JWTComponent;
import hu.psprog.leaflet.security.jwt.exception.InvalidJWTTokenException;
import hu.psprog.leaflet.security.jwt.model.ExtendedUserDetails;
import hu.psprog.leaflet.security.jwt.model.JWTAuthenticationAnswerModel;
import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.jwt.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith(MockitoExtension.class)
public class JWTComponentImplTest {

    private static final String USERNAME = "username";
    private static final List<GrantedAuthority> AUTHORITY_LIST = AuthorityUtils.createAuthorityList("USER");
    private static final String NAME = "User Name";
    private static final long USER_ID = 123L;
    private static final ExtendedUserDetails EXTENDED_USER_DETAILS = ExtendedUserDetails.getBuilder()
            .withUsername(USERNAME)
            .withAuthorities(AUTHORITY_LIST)
            .withName(NAME)
            .withID(USER_ID)
            .build();

    private static final long EXPIRATION_IN_HOURS = 4L;
    private static final String JWT_SECRET = "czNjcjN0";
    private static final String AUTHORIZATION = "Authorization";

    private JWTComponent jwtComponent;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void setup() {
        jwtComponent = new JWTComponentImpl(JWT_SECRET, (int) EXPIRATION_IN_HOURS);
    }

    @Test
    public void shouldGenerateToken() throws IOException {
        
        // when
        JWTAuthenticationAnswerModel result = jwtComponent.generateToken(EXTENDED_USER_DETAILS);
        
        // then
        Map<String, String> jwtPayload = extractJWTPayload(result.getToken());
        assertExpiration(jwtPayload, EXPIRATION_IN_HOURS);
        assertUserInfo(jwtPayload);
    }

    @Test
    public void shouldDecodeValidToken() {

        // given
        JWTAuthenticationAnswerModel generatedToken = jwtComponent.generateToken(EXTENDED_USER_DETAILS);

        // when
        JWTPayload result = jwtComponent.decode(generatedToken.getToken());

        // then
        assertExpiration(result);
        assertUserInfo(result);
    }

    @Test
    public void shouldGenerateTokenWithCustomExpiration() throws IOException {

        // given
        long expiration = 1L;

        // when
        JWTAuthenticationAnswerModel result = jwtComponent.generateToken(EXTENDED_USER_DETAILS, (int) expiration);

        // then
        Map<String, String> jwtPayload = extractJWTPayload(result.getToken());
        assertExpiration(jwtPayload, expiration);
        assertUserInfo(jwtPayload);
    }

    @Test
    public void shouldThrowInvalidJWTTokenExceptionOnInvalidToken() {

        // given
        String token = "invalid-token";

        // when
        Assertions.assertThrows(InvalidJWTTokenException.class, () -> jwtComponent.decode(token));

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

    @Test
    public void shouldThrowExceptionOnExtractTokenIfAuthorizationHeaderIsInvalid() {

        // given
        given(httpServletRequest.getHeader(AUTHORIZATION)).willReturn("Basic auth");

        // when
        String result = jwtComponent.extractToken(httpServletRequest);

        // then
        assertThat(result, nullValue());
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

    private void assertUserInfo(JWTPayload jwtPayload) {
        assertThat(jwtPayload.getUsername(), equalTo(USERNAME));
        assertThat(jwtPayload.getRole(), equalTo(Role.valueOf(AUTHORITY_LIST.get(0).getAuthority())));
        assertThat(jwtPayload.getName(), equalTo(NAME));
        assertThat(jwtPayload.getId(), equalTo((int) USER_ID));
    }

    private void assertExpiration(Map<String, String> jwtPayload, long expiration) {
        long difference = Long.parseLong(jwtPayload.get("exp")) - Long.parseLong(jwtPayload.get("iat"));
        assertThat(TimeUnit.SECONDS.toHours(difference), equalTo(expiration));
    }

    private void assertExpiration(JWTPayload jwtPayload) {
        long difference = jwtPayload.getExpires().getTime() - jwtPayload.getIssuedAt().getTime();
        assertThat(TimeUnit.MILLISECONDS.toHours(difference), equalTo(JWTComponentImplTest.EXPIRATION_IN_HOURS));
    }
}
