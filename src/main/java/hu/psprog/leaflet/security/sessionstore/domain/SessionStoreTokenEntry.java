package hu.psprog.leaflet.security.sessionstore.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Model representing an entry in the session store.
 *
 * @author Peter Smith
 */
public class SessionStoreTokenEntry {
    
    private String token;
    private UUID deviceID;
    private String remoteAddress;
    private String username;
    private TokenStatus status;
    private Timestamp issued;
    private Timestamp expires;

    public String getToken() {
        return token;
    }

    public UUID getDeviceID() {
        return deviceID;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getUsername() {
        return username;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public Timestamp getIssued() {
        return issued;
    }

    public Timestamp getExpires() {
        return expires;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SessionStoreTokenEntry)) return false;

        SessionStoreTokenEntry that = (SessionStoreTokenEntry) o;

        return new EqualsBuilder()
                .append(token, that.token)
                .append(deviceID, that.deviceID)
                .append(remoteAddress, that.remoteAddress)
                .append(username, that.username)
                .append(status, that.status)
                .append(issued, that.issued)
                .append(expires, that.expires)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(token)
                .append(deviceID)
                .append(remoteAddress)
                .append(username)
                .append(status)
                .append(issued)
                .append(expires)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("token", token)
                .append("deviceID", deviceID)
                .append("remoteAddress", remoteAddress)
                .append("username", username)
                .append("status", status)
                .append("issued", issued)
                .append("expires", expires)
                .toString();
    }

    public static SessionStoreTokenEntryBuilder getBuilder() {
        return new SessionStoreTokenEntryBuilder();
    }

    /**
     * Builder for {@link SessionStoreTokenEntry}.
     */
    public static final class SessionStoreTokenEntryBuilder {
        private String token;
        private UUID deviceID;
        private String remoteAddress;
        private String username;
        private TokenStatus status;
        private Timestamp issued;
        private Timestamp expires;

        private SessionStoreTokenEntryBuilder() {
        }

        public SessionStoreTokenEntryBuilder withToken(String token) {
            this.token = token;
            return this;
        }

        public SessionStoreTokenEntryBuilder withDeviceID(UUID deviceID) {
            this.deviceID = deviceID;
            return this;
        }

        public SessionStoreTokenEntryBuilder withRemoteAddress(String remoteAddress) {
            this.remoteAddress = remoteAddress;
            return this;
        }

        public SessionStoreTokenEntryBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public SessionStoreTokenEntryBuilder withStatus(TokenStatus status) {
            this.status = status;
            return this;
        }

        public SessionStoreTokenEntryBuilder withIssued(Timestamp issued) {
            this.issued = issued;
            return this;
        }

        public SessionStoreTokenEntryBuilder withExpires(Timestamp expires) {
            this.expires = expires;
            return this;
        }

        public SessionStoreTokenEntry build() {
            SessionStoreTokenEntry sessionStoreTokenEntry = new SessionStoreTokenEntry();
            sessionStoreTokenEntry.status = this.status;
            sessionStoreTokenEntry.token = this.token;
            sessionStoreTokenEntry.username = this.username;
            sessionStoreTokenEntry.remoteAddress = this.remoteAddress;
            sessionStoreTokenEntry.expires = this.expires;
            sessionStoreTokenEntry.issued = this.issued;
            sessionStoreTokenEntry.deviceID = this.deviceID;
            return sessionStoreTokenEntry;
        }
    }
}
