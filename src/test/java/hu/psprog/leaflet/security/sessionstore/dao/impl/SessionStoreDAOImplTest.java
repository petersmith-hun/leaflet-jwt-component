package hu.psprog.leaflet.security.sessionstore.dao.impl;

import hu.psprog.leaflet.security.sessionstore.config.SessionStoreDataSourceConfiguration;
import hu.psprog.leaflet.security.sessionstore.conversion.SessionStoreTokenEntryConverter;
import hu.psprog.leaflet.security.sessionstore.conversion.SessionStoreTokenEntryMapper;
import hu.psprog.leaflet.security.sessionstore.domain.SessionStoreTokenEntry;
import hu.psprog.leaflet.security.sessionstore.domain.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link SessionStoreDAOImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SessionStoreDAOImplTest.SessionStoreDAOITConfiguration.class)
public class SessionStoreDAOImplTest {

    private static final String INTEGRATION_TEST_DATABASE_SCRIPT = "classpath:session_store_it_db_script.sql";
    private static final SessionStoreTokenEntry SESSION_STORE_TOKEN_ENTRY_1 = prepareSessionStoreTokenEntry(1, TokenStatus.ACTIVE);
    private static final SessionStoreTokenEntry SESSION_STORE_TOKEN_ENTRY_2 = prepareSessionStoreTokenEntry(2, TokenStatus.REVOKED);
    private static final SessionStoreTokenEntry SESSION_STORE_TOKEN_ENTRY_3 = prepareSessionStoreTokenEntry(3, TokenStatus.COMPROMISED);
    private static final SessionStoreTokenEntry SESSION_STORE_TOKEN_ENTRY_TO_INSERT = prepareSessionStoreTokenEntry(4, TokenStatus.ACTIVE);
    private static final String CONTROL_TOKEN = "token-1";

    @Autowired
    private SessionStoreDAOImpl sessionStoreDAO;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @BeforeEach
    public void setup() throws IOException {
        File scriptFile = ResourceUtils.getFile(INTEGRATION_TEST_DATABASE_SCRIPT);
        namedParameterJdbcTemplate.execute(new String(Files.readAllBytes(scriptFile.toPath())), PreparedStatement::execute);
    }

    @Test
    public void shouldGetAllEntries() {

        // when
        List<SessionStoreTokenEntry> result = sessionStoreDAO.getAllTokenEntries();

        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(3));
        assertThat(result.containsAll(Arrays.asList(SESSION_STORE_TOKEN_ENTRY_1, SESSION_STORE_TOKEN_ENTRY_2, SESSION_STORE_TOKEN_ENTRY_3)), is(true));
    }

    @Test
    public void shouldInsertTokenEntry() {

        // when
        sessionStoreDAO.insertTokenEntry(SESSION_STORE_TOKEN_ENTRY_TO_INSERT);

        // then
        List<SessionStoreTokenEntry> current = sessionStoreDAO.getAllTokenEntries();
        assertThat(current.size(), equalTo(4));
        assertThat(current.contains(SESSION_STORE_TOKEN_ENTRY_TO_INSERT), is(true));
    }

    @Test
    public void shouldGetTokenEntryReturnTokenIfExists() {

        // when
        Optional<SessionStoreTokenEntry> result = sessionStoreDAO.getTokenEntry(CONTROL_TOKEN);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(SESSION_STORE_TOKEN_ENTRY_1));
    }

    @Test
    public void shouldGetTokenEntryReturnEmptyOptionalIfNotExists() {

        // when
        Optional<SessionStoreTokenEntry> result = sessionStoreDAO.getTokenEntry("token-not-existing");

        // then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void shouldUpdateTokenEntry() {

        // when
        sessionStoreDAO.updateTokenEntry(CONTROL_TOKEN, TokenStatus.COMPROMISED);

        // then
        assertThat(sessionStoreDAO.getTokenEntry(CONTROL_TOKEN).get().getStatus(), equalTo(TokenStatus.COMPROMISED));
    }

    @Test
    public void shouldRemoveTokenEntry() {

        // when
        sessionStoreDAO.removeTokenEntry(CONTROL_TOKEN);

        // then
        assertThat(sessionStoreDAO.getTokenEntry(CONTROL_TOKEN).isPresent(), is(false));
    }

    private static SessionStoreTokenEntry prepareSessionStoreTokenEntry(int offset, TokenStatus status) {
        return SessionStoreTokenEntry.getBuilder()
                .withToken("token-" + offset)
                .withDeviceID(UUID.fromString(offset + "191f32b-11f4-427f-93ac-d807e4782631"))
                .withRemoteAddress("127.0.0.1")
                .withStatus(status)
                .withUsername("user-" + offset)
                .withIssued(prepareTimestamp(offset))
                .withExpires(prepareTimestamp(offset - 2))
                .build();
    }

    private static Timestamp prepareTimestamp(int offset) {

        Date date = new Calendar.Builder()
                .setDate(2018, 2, 24)
                .setTimeOfDay(19 - offset, 0, 0)
                .build()
                .getTime();

        return new Timestamp(date.getTime());
    }

    @Configuration
    @Import(SessionStoreDataSourceConfiguration.class)
    @ComponentScan("hu.psprog.leaflet.security.sessionstore.dao")
    public static class SessionStoreDAOITConfiguration {

        @Bean
        public SessionStoreTokenEntryConverter sessionStoreTokenEntryConverter() {
            return new SessionStoreTokenEntryConverter();
        }

        @Bean
        public SessionStoreTokenEntryMapper sessionStoreTokenEntryMapper() {
            return new SessionStoreTokenEntryMapper();
        }
    }
}