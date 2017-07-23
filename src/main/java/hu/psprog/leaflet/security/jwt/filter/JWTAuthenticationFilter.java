package hu.psprog.leaflet.security.jwt.filter;

import hu.psprog.leaflet.security.jwt.JWTComponent;
import hu.psprog.leaflet.security.jwt.auth.JWTAuthenticationToken;
import hu.psprog.leaflet.security.jwt.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Authentication filter for JWT-based authentication.
 *
 * @author Peter Smith
 */
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String ANONYMOUS_ID = "ANONYMOUS";
    public static final String DEVICE_ID_HEADER = "X-Device-ID";
    public static final String AUTH_TOKEN_HEADER = "X-Auth-Token";

    private static final String ANONYMOUS_PRINCIPAL = "ANONYMOUS";
    private static final List<GrantedAuthority> ANONYMOUS_ROLE = AuthorityUtils.createAuthorityList(Role.ANONYMOUS.name());
    private static final String URL_ROOT = "/**";

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private JWTComponent jwtComponent;

    public JWTAuthenticationFilter(JWTComponent jwtComponent) {
        super(new AntPathRequestMatcher(URL_ROOT));
        this.jwtComponent = jwtComponent;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        Authentication authentication = new AnonymousAuthenticationToken(ANONYMOUS_ID,
                ANONYMOUS_PRINCIPAL,
                ANONYMOUS_ROLE);

        try {
            String token = jwtComponent.extractToken(request);
            if (Objects.nonNull(token)) {
                authentication = JWTAuthenticationToken.getBuilder()
                        .withRawToken(token)
                        .withPayload(jwtComponent.decode(token))
                        .withDeviceID(extractDeviceID(request))
                        .withRemoteAddress(request.getRemoteAddr())
                        .build();
            }
        } catch (Exception exc) {
            LOGGER.warn("An error occurred while parsing token. Root cause is the following: ", exc);
        }

        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        SecurityContextHolder.getContext().setAuthentication(authResult);
        if (authResult instanceof JWTAuthenticationToken) {
            response.setHeader(AUTH_TOKEN_HEADER, authResult.getCredentials().toString());
        }

        chain.doFilter(request, response);
    }

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    private UUID extractDeviceID(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(DEVICE_ID_HEADER))
                .map(UUID::fromString)
                .orElse(null);
    }
}
