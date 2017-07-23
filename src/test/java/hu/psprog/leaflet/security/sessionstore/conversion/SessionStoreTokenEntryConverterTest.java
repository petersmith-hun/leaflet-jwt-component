package hu.psprog.leaflet.security.sessionstore.conversion;

import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_DEVICE_ID;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_EXPIRES;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_ISSUED;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_REMOTE_ADDRESS;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_STATUS;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_TOKEN;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_USERNAME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link SessionStoreTokenEntryConverter}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionStoreTokenEntryConverterTest {

    private static final String TOKEN = "token";
    private static final UUID DEVICE_ID = UUID.randomUUID();
    private static final String REMOTE_ADDRESS = "remote-address";
    private static final Timestamp EXPIRES = new Timestamp(new Date().getTime() + 60000);
    private static final Timestamp ISSUED = new Timestamp(new Date().getTime());
    private static final TokenStatus TOKEN_STATUS = TokenStatus.ACTIVE;
    private static final String USERNAME = "username";

    @InjectMocks
    private SessionStoreTokenEntryConverter sessionStoreTokenEntryConverter;

    @Test
    public void shouldConvert() {

        // given
        SessionStoreTokenEntry sessionStoreTokenEntry = SessionStoreTokenEntry.getBuilder()
                .withToken(TOKEN)
                .withDeviceID(DEVICE_ID)
                .withRemoteAddress(REMOTE_ADDRESS)
                .withExpires(EXPIRES)
                .withIssued(ISSUED)
                .withStatus(TOKEN_STATUS)
                .withUsername(USERNAME)
                .build();

        // when
        Map<String, Object> result = sessionStoreTokenEntryConverter.convert(sessionStoreTokenEntry);

        // then
        assertThat(result, notNullValue());
        assertThat(result.get(FIELD_TOKEN), equalTo(TOKEN));
        assertThat(result.get(FIELD_DEVICE_ID), equalTo(DEVICE_ID.toString()));
        assertThat(result.get(FIELD_REMOTE_ADDRESS), equalTo(REMOTE_ADDRESS));
        assertThat(result.get(FIELD_USERNAME), equalTo(USERNAME));
        assertThat(result.get(FIELD_STATUS), equalTo(TOKEN_STATUS.name()));
        assertThat(result.get(FIELD_ISSUED), equalTo(ISSUED));
        assertThat(result.get(FIELD_EXPIRES), equalTo(EXPIRES));
    }
}
