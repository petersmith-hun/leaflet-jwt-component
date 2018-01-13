package hu.psprog.leaflet.security.sessionstore.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

/**
 * @author Peter Smith
 */
public class ClaimedTokenContext {

    private UUID deviceID;
    private String remoteAddress;
    private String token;

    public UUID getDeviceID() {
        return deviceID;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getToken() {
        return token;
    }

    public static ClaimedTokenContextBuilder getBuilder() {
        return new ClaimedTokenContextBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ClaimedTokenContext that = (ClaimedTokenContext) o;

        return new EqualsBuilder()
                .append(deviceID, that.deviceID)
                .append(remoteAddress, that.remoteAddress)
                .append(token, that.token)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(deviceID)
                .append(remoteAddress)
                .append(token)
                .toHashCode();
    }

    /**
     * Builder for {@link ClaimedTokenContext}.
     */
    public static final class ClaimedTokenContextBuilder {
        private UUID deviceID;
        private String remoteAddress;
        private String token;

        private ClaimedTokenContextBuilder() {
        }

        public ClaimedTokenContextBuilder withDeviceID(UUID deviceID) {
            this.deviceID = deviceID;
            return this;
        }

        public ClaimedTokenContextBuilder withRemoteAddress(String remoteAddress) {
            this.remoteAddress = remoteAddress;
            return this;
        }

        public ClaimedTokenContextBuilder withToken(String token) {
            this.token = token;
            return this;
        }

        public ClaimedTokenContext build() {
            ClaimedTokenContext claimedTokenContext = new ClaimedTokenContext();
            claimedTokenContext.deviceID = this.deviceID;
            claimedTokenContext.remoteAddress = this.remoteAddress;
            claimedTokenContext.token = this.token;
            return claimedTokenContext;
        }
    }
}
