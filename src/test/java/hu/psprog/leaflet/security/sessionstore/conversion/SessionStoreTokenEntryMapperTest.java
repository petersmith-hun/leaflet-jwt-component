package hu.psprog.leaflet.security.sessionstore.conversion;

import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
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
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link SessionStoreTokenEntryMapper}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionStoreTokenEntryMapperTest {

    private static final String TOKEN = "token";
    private static final UUID DEVICE_ID = UUID.randomUUID();
    private static final String REMOTE_ADDRESS = "remote-address";
    private static final Timestamp EXPIRES = new Timestamp(new Date().getTime() + 60000);
    private static final Timestamp ISSUED = new Timestamp(new Date().getTime());
    private static final TokenStatus TOKEN_STATUS = TokenStatus.ACTIVE;
    private static final String USERNAME = "username";

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private SessionStoreTokenEntryMapper sessionStoreTokenEntryMapper;

    @Test
    public void shouldMapRow() throws SQLException {

        // given
        given(resultSet.getString(FIELD_TOKEN)).willReturn(TOKEN);
        given(resultSet.getString(FIELD_DEVICE_ID)).willReturn(DEVICE_ID.toString());
        given(resultSet.getString(FIELD_REMOTE_ADDRESS)).willReturn(REMOTE_ADDRESS);
        given(resultSet.getString(FIELD_USERNAME)).willReturn(USERNAME);
        given(resultSet.getString(FIELD_STATUS)).willReturn(TOKEN_STATUS.name());
        given(resultSet.getTimestamp(FIELD_EXPIRES)).willReturn(EXPIRES);
        given(resultSet.getTimestamp(FIELD_ISSUED)).willReturn(ISSUED);

        // when
        SessionStoreTokenEntry result = sessionStoreTokenEntryMapper.mapRow(resultSet, 0);

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
