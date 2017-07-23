package hu.psprog.leaflet.security.sessionstore.conversion;

import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_DEVICE_ID;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_EXPIRES;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_ISSUED;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_REMOTE_ADDRESS;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_STATUS;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_TOKEN;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_USERNAME;

/**
 * SQL mapper to read jwt_session_store records as {@link SessionStoreTokenEntry} objects.
 *
 * @author Peter Smith
 */
@Component
public class SessionStoreTokenEntryMapper implements RowMapper<SessionStoreTokenEntry> {

    @Override
    public SessionStoreTokenEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SessionStoreTokenEntry.getBuilder()
                .withToken(rs.getString(FIELD_TOKEN))
                .withDeviceID(UUID.fromString(rs.getString(FIELD_DEVICE_ID)))
                .withRemoteAddress(rs.getString(FIELD_REMOTE_ADDRESS))
                .withUsername(rs.getString(FIELD_USERNAME))
                .withIssued(rs.getTimestamp(FIELD_ISSUED))
                .withExpires(rs.getTimestamp(FIELD_EXPIRES))
                .withStatus(TokenStatus.valueOf(rs.getString(FIELD_STATUS)))
                .build();
    }
}
