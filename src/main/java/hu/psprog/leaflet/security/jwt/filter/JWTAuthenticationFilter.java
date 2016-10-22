package hu.psprog.leaflet.security.jwt.filter;

import hu.psprog.leaflet.security.jwt.auth.JWTAuthenticationToken;
import hu.psprog.leaflet.security.jwt.impl.JWTComponentImpl;
import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.jwt.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Authentication filter for JWT-based authentication.
 *
 * @author Peter Smith
 */
@Component
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String ANONYMOUS_USERNAME = "ANONYMOUS";
    private static final String ANONYMOUS_PASSWORD = "ANONYMOUS";
    private static final GrantedAuthority ANONYMOUS_ROLE = new SimpleGrantedAuthority(Role.ANONYMOUS.name());
    private static final String URL_ROOT = "/**";

    @Autowired
    private JWTComponentImpl jwtComponentImpl;

    public JWTAuthenticationFilter() {
        super(new AntPathRequestMatcher(URL_ROOT));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        String token = jwtComponentImpl.extractToken(request);
        Authentication authentication;

        if (token == null) {
            // authenticate anonymously for public endpoints
            authentication = new AnonymousAuthenticationToken(ANONYMOUS_USERNAME,
                    ANONYMOUS_PASSWORD,
                    Arrays.asList(ANONYMOUS_ROLE));
        } else {
            JWTPayload jwtPayload = jwtComponentImpl.decode(token);
            authentication = new JWTAuthenticationToken(jwtPayload);
        }

        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}
