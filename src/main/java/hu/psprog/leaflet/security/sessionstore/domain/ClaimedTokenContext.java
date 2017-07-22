package hu.psprog.leaflet.security.sessionstore.domain;

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
