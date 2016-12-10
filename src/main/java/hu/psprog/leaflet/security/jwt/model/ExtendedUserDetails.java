package hu.psprog.leaflet.security.jwt.model;

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

    public static class Builder {

        private ExtendedUserDetails extendedUserDetails;

        public Builder() {
            this.extendedUserDetails = new ExtendedUserDetails();
        }

        public Builder withUsername(String username) {
            extendedUserDetails.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            extendedUserDetails.password = password;
            return this;
        }

        public Builder withName(String name) {
            extendedUserDetails.name = name;
            return this;
        }

        public Builder withID(Long id) {
            extendedUserDetails.id = id;
            return this;
        }

        public Builder withEnabled(boolean enabled) {
            extendedUserDetails.enabled = enabled;
            return this;
        }

        public Builder withAuthorities(Collection<GrantedAuthority> authorities) {
            extendedUserDetails.authorities = authorities;
            return this;
        }

        public ExtendedUserDetails build() {
            return extendedUserDetails;
        }
    }
}
