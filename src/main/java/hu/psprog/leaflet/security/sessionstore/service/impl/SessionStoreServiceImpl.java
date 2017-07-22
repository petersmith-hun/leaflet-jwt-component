package hu.psprog.leaflet.security.sessionstore.service.impl;

import hu.psprog.leaflet.security.jwt.auth.JWTAuthenticationToken;
import hu.psprog.leaflet.security.sessionstore.conversion.ClaimedTokenContextConverter;
import hu.psprog.leaflet.security.sessionstore.dao.SessionStoreDAO;
import hu.psprog.leaflet.security.sessionstore.domain.ClaimedTokenContext;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreValidationStatus;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;
import hu.psprog.leaflet.security.sessionstore.service.SessionStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link SessionStoreService}.
 *
 * @author Peter Smith
 */
@Service
class SessionStoreServiceImpl implements SessionStoreService {

    private SessionStoreDAO sessionStoreDAO;
    private ClaimedTokenContextConverter claimedTokenContextConverter;

    @Autowired
    public SessionStoreServiceImpl(SessionStoreDAO sessionStoreDAO, ClaimedTokenContextConverter claimedTokenContextConverter) {
        this.sessionStoreDAO = sessionStoreDAO;
        this.claimedTokenContextConverter = claimedTokenContextConverter;
    }

    @Override
    public void storeToken(ClaimedTokenContext claimedTokenContext) {

        Objects.requireNonNull(claimedTokenContext.getToken(), "Token cannot be null!");
        Objects.requireNonNull(claimedTokenContext.getDeviceID(), "Device ID cannot be null!");
        Objects.requireNonNull(claimedTokenContext.getRemoteAddress(), "Remote address cannot be null");

        SessionStoreTokenEntry sessionStoreTokenEntry = claimedTokenContextConverter.convert(claimedTokenContext);
        sessionStoreDAO.insertTokenEntry(sessionStoreTokenEntry);
    }

    @Override
    public SessionStoreValidationStatus validateToken(JWTAuthenticationToken jwtAuthenticationToken) {
        return sessionStoreDAO.getTokenEntry(jwtAuthenticationToken.getRawToken())
                .map(sessionStoreTokenEntry -> validate(jwtAuthenticationToken, sessionStoreTokenEntry))
                .orElse(SessionStoreValidationStatus.UNKNOWN_TOKEN);
    }

    @Override
    public void revokeToken(JWTAuthenticationToken jwtAuthenticationToken) {
        sessionStoreDAO.updateTokenEntry(jwtAuthenticationToken.getRawToken(), TokenStatus.REVOKED);
    }

    @Override
    public void cleanExpiredToken(int threshold) {
        sessionStoreDAO.getAllTokenEntries().stream()
                .filter(sessionStoreTokenEntry -> getExpirationInMinutes(sessionStoreTokenEntry) > threshold)
                .map(SessionStoreTokenEntry::getToken)
                .forEach(sessionStoreDAO::removeTokenEntry);
    }

    private SessionStoreValidationStatus validate(JWTAuthenticationToken jwtAuthenticationToken, SessionStoreTokenEntry sessionStoreTokenEntry) {

        SessionStoreValidationStatus validationResult = SessionStoreValidationStatus.VALID;
        if (sessionStoreTokenEntry.getStatus() != TokenStatus.ACTIVE) {
            validationResult = SessionStoreValidationStatus.INVALIDATED;
        } else if (!isSourceValid(jwtAuthenticationToken, sessionStoreTokenEntry)) {
            validationResult = SessionStoreValidationStatus.DIFFERENT_SOURCE;
            sessionStoreDAO.updateTokenEntry(sessionStoreTokenEntry.getToken(), TokenStatus.COMPROMISED);
        }

        return validationResult;
    }

    private boolean isSourceValid(JWTAuthenticationToken jwtAuthenticationToken, SessionStoreTokenEntry sessionStoreTokenEntry) {
        return isDeviceIDValid(jwtAuthenticationToken, sessionStoreTokenEntry)
                && isRemoteAddressValid(jwtAuthenticationToken, sessionStoreTokenEntry);
    }

    private boolean isDeviceIDValid(JWTAuthenticationToken jwtAuthenticationToken, SessionStoreTokenEntry sessionStoreTokenEntry) {

        Objects.requireNonNull(jwtAuthenticationToken.getDeviceID(), "Device ID cannot be null.");

        return jwtAuthenticationToken.getDeviceID().equals(sessionStoreTokenEntry.getDeviceID());
    }

    private boolean isRemoteAddressValid(JWTAuthenticationToken jwtAuthenticationToken, SessionStoreTokenEntry sessionStoreTokenEntry) {
        return jwtAuthenticationToken.getRemoteAddress().equals(sessionStoreTokenEntry.getRemoteAddress());
    }

    private long getExpirationInMinutes(SessionStoreTokenEntry sessionStoreTokenEntry) {

        long difference = System.currentTimeMillis() - sessionStoreTokenEntry.getExpires().getTime();

        return TimeUnit.MILLISECONDS.toMinutes(difference);
    }
}
