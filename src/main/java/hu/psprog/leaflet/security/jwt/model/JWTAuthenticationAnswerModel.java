package hu.psprog.leaflet.security.jwt.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Peter Smith
 */
public class JWTAuthenticationAnswerModel {

    private String token;

    public JWTAuthenticationAnswerModel() {
        // Serializable
    }

    public JWTAuthenticationAnswerModel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("token", token)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof JWTAuthenticationAnswerModel)) return false;

        JWTAuthenticationAnswerModel that = (JWTAuthenticationAnswerModel) o;

        return new EqualsBuilder()
                .append(token, that.token)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(token)
                .toHashCode();
    }
}
