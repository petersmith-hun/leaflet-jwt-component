package hu.psprog.leaflet.security.jwt.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Model containing generated token.
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = JWTAuthenticationAnswerModel.JWTAuthenticationAnswerModelBuilder.class)
public class JWTAuthenticationAnswerModel {

    private String token;

    public String getToken() {
        return token;
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

    public static JWTAuthenticationAnswerModelBuilder getBuilder() {
        return new JWTAuthenticationAnswerModelBuilder();
    }

    /**
     * Builder for {@link JWTAuthenticationAnswerModel}.
     */
    public static final class JWTAuthenticationAnswerModelBuilder {
        private String token;

        private JWTAuthenticationAnswerModelBuilder() {
        }

        public JWTAuthenticationAnswerModelBuilder withToken(String token) {
            this.token = token;
            return this;
        }

        public JWTAuthenticationAnswerModel build() {
            JWTAuthenticationAnswerModel jWTAuthenticationAnswerModel = new JWTAuthenticationAnswerModel();
            jWTAuthenticationAnswerModel.token = this.token;
            return jWTAuthenticationAnswerModel;
        }
    }
}
