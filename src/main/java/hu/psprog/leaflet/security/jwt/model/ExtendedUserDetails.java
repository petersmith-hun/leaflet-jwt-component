package hu.psprog.leaflet.security.jwt.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Spring Security compatible {@link UserDetails} implementation containing extra user informations.
 *
 * @author Peter Smith
 */
public class ExtendedUserDetails implements UserDetails {

    private String username;
    private String password;
    private String name;
    private Long id;
    private boolean enabled;
    private Collection<GrantedAuthority> authorities;

    private ExtendedUserDetails() {
        // prevent direct initialization
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ExtendedUserDetails that = (ExtendedUserDetails) o;

        return new EqualsBuilder()
                .append(enabled, that.enabled)
                .append(username, that.username)
                .append(password, that.password)
                .append(name, that.name)
                .append(id, that.id)
                .append(authorities, that.authorities)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(username)
                .append(password)
                .append(name)
                .append(id)
                .append(enabled)
                .append(authorities)
                .toHashCode();
    }

    public static ExtendedUserDetailsBuilder getBuilder() {
        return new ExtendedUserDetailsBuilder();
    }

    /**
     * Builder for {@link ExtendedUserDetails}.
     */
    public static class ExtendedUserDetailsBuilder {

        private ExtendedUserDetails extendedUserDetails;

        private ExtendedUserDetailsBuilder() {
            this.extendedUserDetails = new ExtendedUserDetails();
        }

        public ExtendedUserDetailsBuilder withUsername(String username) {
            extendedUserDetails.username = username;
            return this;
        }

        public ExtendedUserDetailsBuilder withPassword(String password) {
            extendedUserDetails.password = password;
            return this;
        }

        public ExtendedUserDetailsBuilder withName(String name) {
            extendedUserDetails.name = name;
            return this;
        }

        public ExtendedUserDetailsBuilder withID(Long id) {
            extendedUserDetails.id = id;
            return this;
        }

        public ExtendedUserDetailsBuilder withEnabled(boolean enabled) {
            extendedUserDetails.enabled = enabled;
            return this;
        }

        public ExtendedUserDetailsBuilder withAuthorities(Collection<GrantedAuthority> authorities) {
            extendedUserDetails.authorities = authorities;
            return this;
        }

        public ExtendedUserDetails build() {
            return extendedUserDetails;
        }
    }
}
