package hu.psprog.leaflet.security.jwt.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * JWT content wrapper.
 */
@JsonDeserialize(builder = JWTPayload.JWTPayloadBuilder.class)
public class JWTPayload implements Serializable {

    /**
     * Standard Issued at field in payload ("iat").
     */
    @NotNull
    private Date issuedAt;

    /**
     * Standard Expiration time field in payload ("exp");
     */
    @NotNull
    private Date expires;

    /**
     * Username of user who claimed the token.
     */
    @NotNull
    private String username;

    /**
     * User role.
     */
    @NotNull
    private Role role;

    /**
     * User's public name.
     */
    @NotNull
    private String name;

    /**
     * User ID.
     */
    @NotNull
    private Integer id;

    public Date getIssuedAt() {
        return issuedAt;
    }

    public Date getExpires() {
        return expires;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("issuedAt", issuedAt)
                .append("expires", expires)
                .append("username", username)
                .append("role", role)
                .append("name", name)
                .append("id", id)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof JWTPayload)) return false;

        JWTPayload that = (JWTPayload) o;

        return new EqualsBuilder()
                .append(issuedAt, that.issuedAt)
                .append(expires, that.expires)
                .append(username, that.username)
                .append(role, that.role)
                .append(name, that.name)
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(issuedAt)
                .append(expires)
                .append(username)
                .append(role)
                .append(name)
                .append(id)
                .toHashCode();
    }

    public static JWTPayloadBuilder getBuilder() {
        return new JWTPayloadBuilder();
    }

    /**
     * Builder for {@link JWTPayload}.
     */
    public static final class JWTPayloadBuilder {
        private Date issuedAt;
        private Date expires;
        private String username;
        private Role role;
        private String name;
        private Integer id;

        private JWTPayloadBuilder() {
        }

        public JWTPayloadBuilder withIssuedAt(Date issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public JWTPayloadBuilder withExpires(Date expires) {
            this.expires = expires;
            return this;
        }

        public JWTPayloadBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public JWTPayloadBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        public JWTPayloadBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public JWTPayloadBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public JWTPayload build() {
            JWTPayload jWTPayload = new JWTPayload();
            jWTPayload.username = this.username;
            jWTPayload.expires = this.expires;
            jWTPayload.role = this.role;
            jWTPayload.issuedAt = this.issuedAt;
            jWTPayload.name = this.name;
            jWTPayload.id = this.id;
            return jWTPayload;
        }
    }
}
