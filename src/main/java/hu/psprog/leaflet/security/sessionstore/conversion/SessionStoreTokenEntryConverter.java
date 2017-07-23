package hu.psprog.leaflet.security.sessionstore.conversion;

import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_DEVICE_ID;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_EXPIRES;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_ISSUED;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_REMOTE_ADDRESS;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_STATUS;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_TOKEN;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_USERNAME;

/**
 * Maps a {@link SessionStoreTokenEntry} object to a {@link Map} of parameters for write operations.
 *
 * @author Peter Smith
 */
@Component
public class SessionStoreTokenEntryConverter implements Converter<SessionStoreTokenEntry, Map<String, Object>> {

    public Map<String, Object> convert(SessionStoreTokenEntry sessionStoreTokenEntry) {

        Map<String, Object> insertToken = new HashMap<>();
        insertToken.put(FIELD_TOKEN, sessionStoreTokenEntry.getToken());
        insertToken.put(FIELD_DEVICE_ID, sessionStoreTokenEntry.getDeviceID().toString());
        insertToken.put(FIELD_REMOTE_ADDRESS, sessionStoreTokenEntry.getRemoteAddress());
        insertToken.put(FIELD_USERNAME, sessionStoreTokenEntry.getUsername());
        insertToken.put(FIELD_STATUS, sessionStoreTokenEntry.getStatus().name());
        insertToken.put(FIELD_ISSUED, sessionStoreTokenEntry.getIssued());
        insertToken.put(FIELD_EXPIRES, sessionStoreTokenEntry.getExpires());

        return insertToken;
    }
}
