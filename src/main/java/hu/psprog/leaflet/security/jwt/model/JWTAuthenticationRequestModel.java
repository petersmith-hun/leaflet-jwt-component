package hu.psprog.leaflet.security.jwt.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Peter Smith
 */
public class JWTAuthenticationRequestModel {

    private String username;
    private String password;

    public JWTAuthenticationRequestModel() {
        // Serializable
    }

    public JWTAuthenticationRequestModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("username", username)
                .append("password", password)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof JWTAuthenticationRequestModel)) return false;

        JWTAuthenticationRequestModel that = (JWTAuthenticationRequestModel) o;

        return new EqualsBuilder()
                .append(username, that.username)
                .append(password, that.password)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(username)
                .append(password)
                .toHashCode();
    }
}
