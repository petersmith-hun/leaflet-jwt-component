package hu.psprog.leaflet.security.sessionstore.dao.impl;

import hu.psprog.leaflet.security.sessionstore.conversion.SessionStoreTokenEntryConverter;
import hu.psprog.leaflet.security.sessionstore.conversion.SessionStoreTokenEntryMapper;
import hu.psprog.leaflet.security.sessionstore.dao.SessionStoreDAO;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_STATUS;
import static hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration.FIELD_TOKEN;

/**
 * Implementation of {@link SessionStoreDAO}.
 *
 * @author Peter Smith
 */
@Repository
class SessionStoreDAOImpl implements SessionStoreDAO {

    private static final String GET_ALL_TOKENS =
            "SELECT * "
            + "FROM jwt_session_store ";

    private static final String INSERT_TOKEN =
            "INSERT INTO jwt_session_store "
            + "(token, device_id, remote_address, username, status, issued, expires) "
            + "VALUES "
            + "(:token, :device_id, :remote_address, :username, :status, :issued, :expires);";

    private static final String GET_TOKEN =
            "SELECT * "
            + "FROM jwt_session_store "
            + "WHERE token = :token;";

    private static final String UPDATE_TOKEN =
            "UPDATE jwt_session_store "
            + "SET status = :status "
            + "WHERE token = :token;";

    private static final String REMOVE_TOKEN =
            "DELETE FROM jwt_session_store "
            + "WHERE token = :token;";

    private static final PreparedStatementCallback<Boolean> PREPARED_STATEMENT_CALLBACK = PreparedStatement::execute;

    private SessionStoreTokenEntryConverter sessionStoreTokenEntryConverter;
    private SessionStoreTokenEntryMapper sessionStoreTokenEntryMapper;
    private NamedParameterJdbcTemplate sessionStoreJDBCTemplate;

    @Autowired
    public SessionStoreDAOImpl(@Qualifier("sessionStoreJDBCTemplate") NamedParameterJdbcTemplate sessionStoreJDBCTemplate,
                               SessionStoreTokenEntryConverter sessionStoreTokenEntryConverter,
                               SessionStoreTokenEntryMapper sessionStoreTokenEntryMapper) {
        this.sessionStoreTokenEntryConverter = sessionStoreTokenEntryConverter;
        this.sessionStoreJDBCTemplate = sessionStoreJDBCTemplate;
        this.sessionStoreTokenEntryMapper = sessionStoreTokenEntryMapper;
    }

    @Override
    public List<SessionStoreTokenEntry> getAllTokenEntries() {
        return sessionStoreJDBCTemplate.query(GET_ALL_TOKENS, sessionStoreTokenEntryMapper);
    }

    @Override
    public void insertTokenEntry(SessionStoreTokenEntry sessionStoreTokenEntry) {
        Map<String, Object> insertTokenParamMap = sessionStoreTokenEntryConverter.convert(sessionStoreTokenEntry);
        sessionStoreJDBCTemplate.execute(INSERT_TOKEN, insertTokenParamMap, PREPARED_STATEMENT_CALLBACK);
    }

    @Override
    public Optional<SessionStoreTokenEntry> getTokenEntry(String token) {

        SessionStoreTokenEntry tokenEntry = null;
        List<SessionStoreTokenEntry> result = sessionStoreJDBCTemplate.query(GET_TOKEN, paramMap(token), sessionStoreTokenEntryMapper);
        if (result.size() == 1) {
            tokenEntry = result.get(0);
        }

        return Optional.ofNullable(tokenEntry);
    }

    @Override
    public void updateTokenEntry(String token, TokenStatus status) {
        sessionStoreJDBCTemplate.execute(UPDATE_TOKEN, paramMap(token, status), PREPARED_STATEMENT_CALLBACK);
    }

    @Override
    public void removeTokenEntry(String token) {
        sessionStoreJDBCTemplate.execute(REMOVE_TOKEN, paramMap(token), PREPARED_STATEMENT_CALLBACK);
    }

    private Map<String, Object> paramMap(String token) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(FIELD_TOKEN, token);

        return paramMap;
    }

    private Map<String, Object> paramMap(String token, TokenStatus status) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(FIELD_TOKEN, token);
        paramMap.put(FIELD_STATUS, status.name());

        return paramMap;
    }
}
