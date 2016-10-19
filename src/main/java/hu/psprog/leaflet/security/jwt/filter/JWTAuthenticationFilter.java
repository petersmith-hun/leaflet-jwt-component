package hu.psprog.leaflet.security.jwt.filter;

import hu.psprog.leaflet.security.jwt.auth.JWTAuthenticationToken;
import hu.psprog.leaflet.security.jwt.util.JWTUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication filter for JWT-based authentication.
 *
 * @author Peter Smith
 */
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String URL_ROOT = "/**";

    public JWTAuthenticationFilter() {
        super(new AntPathRequestMatcher(URL_ROOT));
    }

    @Override
    public Authentication attemptAuthentication(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        String token = JWTUtility.extractToken(request);
        Authentication authentication = new JWTAuthenticationToken(token);

        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }
}
