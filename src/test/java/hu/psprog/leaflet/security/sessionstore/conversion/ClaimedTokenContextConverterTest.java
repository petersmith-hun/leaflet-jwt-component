package hu.psprog.leaflet.security.sessionstore.conversion;

import hu.psprog.leaflet.security.jwt.JWTComponent;
import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.sessionstore.domain.ClaimedTokenContext;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link ClaimedTokenContextConverter}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class ClaimedTokenContextConverterTest {

    private static final String TOKEN = "token";
    private static final UUID DEVICE_ID = UUID.randomUUID();
    private static final String REMOTE_ADDRESS = "remote-address";
    private static final Timestamp EXPIRES = new Timestamp(new Date().getTime() + 60000);
    private static final Timestamp ISSUED = new Timestamp(new Date().getTime());
    private static final TokenStatus TOKEN_STATUS = TokenStatus.ACTIVE;
    private static final String USERNAME = "username";

    @Mock
    private JWTComponent jwtComponent;

    @InjectMocks
    private ClaimedTokenContextConverter claimedTokenContextConverter;

    @Test
    public void shouldConvert() {

        // given
        ClaimedTokenContext claimedTokenContext = ClaimedTokenContext.getBuilder()
                .withToken(TOKEN)
                .withDeviceID(DEVICE_ID)
                .withRemoteAddress(REMOTE_ADDRESS)
                .build();
        JWTPayload jwtPayload = JWTPayload.getBuilder()
                .withUsername(USERNAME)
                .withIssuedAt(ISSUED)
                .withExpires(EXPIRES)
                .build();
        given(jwtComponent.decode(TOKEN)).willReturn(jwtPayload);

        // when
        SessionStoreTokenEntry result = claimedTokenContextConverter.convert(claimedTokenContext);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getToken(), equalTo(TOKEN));
        assertThat(result.getDeviceID(), equalTo(DEVICE_ID));
        assertThat(result.getRemoteAddress(), equalTo(REMOTE_ADDRESS));
        assertThat(result.getUsername(), equalTo(USERNAME));
        assertThat(result.getIssued(), equalTo(ISSUED));
        assertThat(result.getExpires(), equalTo(EXPIRES));
        assertThat(result.getStatus(), equalTo(TOKEN_STATUS));
    }
}
