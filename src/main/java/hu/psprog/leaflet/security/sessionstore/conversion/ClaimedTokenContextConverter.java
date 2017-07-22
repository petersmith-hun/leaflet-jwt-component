package hu.psprog.leaflet.security.sessionstore.conversion;

import hu.psprog.leaflet.security.jwt.JWTComponent;
import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.sessionstore.domain.ClaimedTokenContext;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Converts {@link ClaimedTokenContext} to {@link SessionStoreTokenEntry}.
 *
 * @author Peter Smith
 */
@Component
public class ClaimedTokenContextConverter implements Converter<ClaimedTokenContext, SessionStoreTokenEntry> {

    private JWTComponent jwtComponent;

    @Autowired
    public ClaimedTokenContextConverter(JWTComponent jwtComponent) {
        this.jwtComponent = jwtComponent;
    }

    @Override
    public SessionStoreTokenEntry convert(ClaimedTokenContext claimedTokenContext) {

        JWTPayload payload = jwtComponent.decode(claimedTokenContext.getToken());

        return SessionStoreTokenEntry.getBuilder()
                .withToken(claimedTokenContext.getToken())
                .withDeviceID(claimedTokenContext.getDeviceID())
                .withRemoteAddress(claimedTokenContext.getRemoteAddress())
                .withUsername(payload.getUsername())
                .withIssued(convertTimestamp(payload.getIssuedAt()))
                .withExpires(convertTimestamp(payload.getExpires()))
                .withStatus(TokenStatus.ACTIVE)
                .build();
    }

    private Timestamp convertTimestamp(Date source) {
        return new Timestamp(source.getTime());
    }
}
