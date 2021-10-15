package hu.psprog.leaflet.security.sessionstore.service.impl;

import hu.psprog.leaflet.security.jwt.auth.JWTAuthenticationToken;
import hu.psprog.leaflet.security.sessionstore.conversion.ClaimedTokenContextConverter;
import hu.psprog.leaflet.security.sessionstore.dao.SessionStoreDAO;
import hu.psprog.leaflet.security.sessionstore.domain.ClaimedTokenContext;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreValidationStatus;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link SessionStoreServiceImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class SessionStoreServiceImplTest {

    private static final String TOKEN = "token";
    private static final UUID DEVICE_ID = UUID.randomUUID();
    private static final String REMOTE_ADDRESS = "remote-address";

    @Mock
    private SessionStoreDAO sessionStoreDAO;

    @Mock
    private ClaimedTokenContextConverter claimedTokenContextConverter;

    @Mock
    private JWTAuthenticationToken jwtAuthenticationToken;

    @InjectMocks
    private SessionStoreServiceImpl sessionStoreService;

    @Test
    public void shouldStoreToken() {

        // given
        SessionStoreTokenEntry sessionStoreTokenEntry = SessionStoreTokenEntry.getBuilder().build();
        ClaimedTokenContext claimedTokenContext = ClaimedTokenContext.getBuilder()
                .withToken("token")
                .withDeviceID(UUID.randomUUID())
                .withRemoteAddress("remote-address")
                .build();
        given(claimedTokenContextConverter.convert(claimedTokenContext)).willReturn(sessionStoreTokenEntry);

        // when
        sessionStoreService.storeToken(claimedTokenContext);

        // then
        verify(claimedTokenContextConverter).convert(claimedTokenContext);
        verify(sessionStoreDAO).insertTokenEntry(sessionStoreTokenEntry);
    }

    @Test
    public void shouldThrowNPEOnMissingToken() {

        // given
        ClaimedTokenContext claimedTokenContext = ClaimedTokenContext.getBuilder()
                .withDeviceID(UUID.randomUUID())
                .withRemoteAddress("remote-address")
                .build();

        // when
        Assertions.assertThrows(NullPointerException.class, () -> sessionStoreService.storeToken(claimedTokenContext));

        // then
        // expected exception
        verifyNoInteractions(sessionStoreDAO);
    }

    @Test
    public void shouldThrowNPEOnMissingDeviceID() {

        // given
        ClaimedTokenContext claimedTokenContext = ClaimedTokenContext.getBuilder()
                .withToken("token")
                .withRemoteAddress("remote-address")
                .build();

        // when
        Assertions.assertThrows(NullPointerException.class, () -> sessionStoreService.storeToken(claimedTokenContext));

        // then
        // expected exception
        verifyNoInteractions(sessionStoreDAO);
    }

    @Test
    public void shouldThrowNPEOnMissingRemoteAddress() {

        // given
        ClaimedTokenContext claimedTokenContext = ClaimedTokenContext.getBuilder()
                .withToken("token")
                .withDeviceID(UUID.randomUUID())
                .build();

        // when
        Assertions.assertThrows(NullPointerException.class, () -> sessionStoreService.storeToken(claimedTokenContext));

        // then
        // expected exception
        verifyNoInteractions(sessionStoreDAO);
    }

    @Test
    public void shouldValidateTokenWithSuccess() {

        // given
        SessionStoreTokenEntry sessionStoreTokenEntry = prepareSessionStoreTokenEntry(true);
        given(jwtAuthenticationToken.getRawToken()).willReturn(TOKEN);
        given(jwtAuthenticationToken.getDeviceID()).willReturn(DEVICE_ID);
        given(jwtAuthenticationToken.getRemoteAddress()).willReturn(REMOTE_ADDRESS);
        given(sessionStoreDAO.getTokenEntry(anyString())).willReturn(Optional.of(sessionStoreTokenEntry));

        // when
        SessionStoreValidationStatus result = sessionStoreService.validateToken(jwtAuthenticationToken);

        // then
        assertThat(result, equalTo(SessionStoreValidationStatus.VALID));
    }

    @Test
    public void shouldValidateTokenWithInvalidatedOnNonActiveToken() {

        // given
        SessionStoreTokenEntry sessionStoreTokenEntry = prepareSessionStoreTokenEntry(false);
        given(jwtAuthenticationToken.getRawToken()).willReturn(TOKEN);
        given(sessionStoreDAO.getTokenEntry(anyString())).willReturn(Optional.of(sessionStoreTokenEntry));

        // when
        SessionStoreValidationStatus result = sessionStoreService.validateToken(jwtAuthenticationToken);

        // then
        assertThat(result, equalTo(SessionStoreValidationStatus.INVALIDATED));
    }

    @Test
    public void shouldValidateTokenWithDifferentSourceOnInvalidRemoteAddress() {

        // given
        SessionStoreTokenEntry sessionStoreTokenEntry = prepareSessionStoreTokenEntry(true);
        given(jwtAuthenticationToken.getRawToken()).willReturn(TOKEN);
        given(jwtAuthenticationToken.getDeviceID()).willReturn(DEVICE_ID);
        given(jwtAuthenticationToken.getRemoteAddress()).willReturn("other remote address");
        given(sessionStoreDAO.getTokenEntry(anyString())).willReturn(Optional.of(sessionStoreTokenEntry));

        // when
        SessionStoreValidationStatus result = sessionStoreService.validateToken(jwtAuthenticationToken);

        // then
        assertThat(result, equalTo(SessionStoreValidationStatus.DIFFERENT_SOURCE));
        verify(sessionStoreDAO).updateTokenEntry(TOKEN, TokenStatus.COMPROMISED);
    }

    @Test
    public void shouldValidateTokenWithDifferentSourceOnInvalidDeviceID() {

        // given
        SessionStoreTokenEntry sessionStoreTokenEntry = prepareSessionStoreTokenEntry(true);
        given(jwtAuthenticationToken.getRawToken()).willReturn(TOKEN);
        given(jwtAuthenticationToken.getDeviceID()).willReturn(UUID.randomUUID());
        given(sessionStoreDAO.getTokenEntry(anyString())).willReturn(Optional.of(sessionStoreTokenEntry));

        // when
        SessionStoreValidationStatus result = sessionStoreService.validateToken(jwtAuthenticationToken);

        // then
        assertThat(result, equalTo(SessionStoreValidationStatus.DIFFERENT_SOURCE));
        verify(sessionStoreDAO).updateTokenEntry(TOKEN, TokenStatus.COMPROMISED);
    }

    @Test
    public void shouldRevokeToken() {

        // given
        given(jwtAuthenticationToken.getRawToken()).willReturn(TOKEN);

        // when
        sessionStoreService.revokeToken(jwtAuthenticationToken);

        // then
        verify(sessionStoreDAO).updateTokenEntry(TOKEN, TokenStatus.REVOKED);
    }

    @Test
    public void shouldCleanExpiredTokenWith2mThreshold() {

        // given
        int threshold = 2;
        List<SessionStoreTokenEntry> entries = prepareSessionStoreTokenEntries();
        given(sessionStoreDAO.getAllTokenEntries()).willReturn(entries);

        // when
        sessionStoreService.cleanExpiredToken(threshold);

        // then
        verify(sessionStoreDAO, times(5)).removeTokenEntry(anyString());
        verify(sessionStoreDAO).removeTokenEntry(entries.get(2).getToken());
        verify(sessionStoreDAO).removeTokenEntry(entries.get(3).getToken());
        verify(sessionStoreDAO).removeTokenEntry(entries.get(4).getToken());
        verify(sessionStoreDAO).removeTokenEntry(entries.get(5).getToken());
        verify(sessionStoreDAO).removeTokenEntry(entries.get(6).getToken());
    }

    @Test
    public void shouldCleanExpiredTokenWith60mThreshold() {

        // given
        int threshold = 60;
        List<SessionStoreTokenEntry> entries = prepareSessionStoreTokenEntries();
        given(sessionStoreDAO.getAllTokenEntries()).willReturn(entries);

        // when
        sessionStoreService.cleanExpiredToken(threshold);

        // then
        verify(sessionStoreDAO, times(3)).removeTokenEntry(anyString());
        verify(sessionStoreDAO).removeTokenEntry(entries.get(4).getToken());
        verify(sessionStoreDAO).removeTokenEntry(entries.get(5).getToken());
        verify(sessionStoreDAO).removeTokenEntry(entries.get(6).getToken());
    }

    @Test
    public void shouldCleanExpiredTokenWith130mThreshold() {

        // given
        int threshold = 130;
        List<SessionStoreTokenEntry> entries = prepareSessionStoreTokenEntries();
        given(sessionStoreDAO.getAllTokenEntries()).willReturn(entries);

        // when
        sessionStoreService.cleanExpiredToken(threshold);

        // then
        verify(sessionStoreDAO, never()).removeTokenEntry(anyString());
    }

    private List<SessionStoreTokenEntry> prepareSessionStoreTokenEntries() {
        return Arrays.asList(
                prepareSessionStoreTokenEntry(90),
                prepareSessionStoreTokenEntry(30),
                prepareSessionStoreTokenEntry(-4),
                prepareSessionStoreTokenEntry(-10),
                prepareSessionStoreTokenEntry(-62),
                prepareSessionStoreTokenEntry(-70),
                prepareSessionStoreTokenEntry(-120));
    }

    private SessionStoreTokenEntry prepareSessionStoreTokenEntry(int expirationInMinutes) {
        return SessionStoreTokenEntry.getBuilder()
                .withToken("token_" + expirationInMinutes)
                .withExpires(new Timestamp(getTimeWithOffsetInMinutes(expirationInMinutes)))
                .build();
    }

    private long getTimeWithOffsetInMinutes(int expirationInMinutes) {
        return System.currentTimeMillis() + expirationInMinutes * 60000L;
    }

    private SessionStoreTokenEntry prepareSessionStoreTokenEntry(boolean isActive) {
        return SessionStoreTokenEntry.getBuilder()
                .withStatus(isActive ? TokenStatus.ACTIVE : TokenStatus.REVOKED)
                .withToken(TOKEN)
                .withDeviceID(DEVICE_ID)
                .withRemoteAddress(REMOTE_ADDRESS)
                .build();
    }
}
