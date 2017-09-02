package hu.psprog.leaflet.security.sessionstore.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Session Store data source configuration.
 *
 * @author Peter Smith
 */
@Configuration
public class SessionStoreDataSourceConfiguration {

    private static final String INIT_SCRIPT = "classpath:jwt_session_store_init.sql";
    private static final String DATABASE_NAME = "session-store";

    public static final String FIELD_TOKEN = "token";
    public static final String FIELD_DEVICE_ID = "device_id";
    public static final String FIELD_REMOTE_ADDRESS = "remote_address";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_ISSUED = "issued";
    public static final String FIELD_EXPIRES = "expires";

    @Bean
    public NamedParameterJdbcTemplate sessionStoreJDBCTemplate() {
        return new NamedParameterJdbcTemplate(sessionStoreDataSource());
    }


    @Bean
    @DependsOn("sessionStoreJDBCTemplate")
    @Profile({"development", "nossl"})
    public Server h2Server() throws SQLException {
        return Server
                .createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9955")
                .start();
    }

    private DataSource sessionStoreDataSource() {

        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName(DATABASE_NAME)
                .addScript(INIT_SCRIPT)
                .build();
    }
}
