package hu.psprog.leaflet.security.jwt.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * JWT content wrapper.
 */
public class JWTPayload implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public JWTPayload() {
        // Serializable
    }

    public JWTPayload(Date issuedAt, Date expires, String username, Role role, String name, Integer id) {
        super();
        this.issuedAt = issuedAt;
        this.expires = expires;
        this.username = username;
        this.role = role;
        this.name = name;
        this.id = id;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
