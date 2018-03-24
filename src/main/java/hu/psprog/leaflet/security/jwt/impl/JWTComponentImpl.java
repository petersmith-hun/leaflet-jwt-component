package hu.psprog.leaflet.security.jwt.impl;

import hu.psprog.leaflet.security.jwt.JWTComponent;
import hu.psprog.leaflet.security.jwt.exception.InvalidJWTTokenException;
import hu.psprog.leaflet.security.jwt.model.ExtendedUserDetails;
import hu.psprog.leaflet.security.jwt.model.JWTAuthenticationAnswerModel;
import hu.psprog.leaflet.security.jwt.model.JWTPayload;
import hu.psprog.leaflet.security.jwt.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * JWT encoder/decoder utility.
 *
 * Application specific JWT secret and expiration shall be provided in external application configuration file.
 */
@Component
public class JWTComponentImpl implements JWTComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTComponentImpl.class);

    private static final String TOKEN_CAN_NOT_BE_DECODED = "Token can not be decoded because of the following reason: ";

    private static final String JWT_USERNAME = "usr";
    private static final String JWT_USER_ROLE = "rol";
    private static final String JWT_USER_PUBLIC_NAME = "name";
    private static final String JWT_USER_ID = "uid";

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_BEARER = "Bearer ";

    private String jwtSecret;
    private Integer expirationInHours;

    @Autowired
    public JWTComponentImpl(String jwtSecret, Integer expirationInHours) {
        this.jwtSecret = jwtSecret;
        this.expirationInHours = expirationInHours;
    }

    /**
     * Generates token from {@link UserDetails} object with default expiration length.
     *
     * @param userDetails {@link UserDetails} object to generate token based on
     * @return token wrapped in {@link JWTAuthenticationAnswerModel} object
     */
    @Override
    public JWTAuthenticationAnswerModel generateToken(UserDetails userDetails) {
        return generateToken(userDetails, expirationInHours);
    }

    /**
     * Generates token from {@link UserDetails} object with custom expiration length.
     *
     * @param userDetails {@link UserDetails} object to generate token based on
     * @param expiration expiration length in hours
     * @return token wrapped in {@link JWTAuthenticationAnswerModel} object
     */
    @Override
    public JWTAuthenticationAnswerModel generateToken(UserDetails userDetails, Integer expiration) {
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date issuedAt = new Date();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JWT_USERNAME, userDetails.getUsername());
        claims.put(JWT_USER_ROLE, roles);
        claims.put(JWT_USER_PUBLIC_NAME, ((ExtendedUserDetails) userDetails).getName());
        claims.put(JWT_USER_ID, ((ExtendedUserDetails) userDetails).getId().intValue());

        return JWTAuthenticationAnswerModel.getBuilder()
                .withToken(Jwts.builder()
                        .setExpiration(generateExpiration(issuedAt, expiration))
                        .setIssuedAt(issuedAt)
                        .addClaims(claims)
                        .signWith(SignatureAlgorithm.HS512, jwtSecret)
                        .compact())
                .build();
    }

    /**
     * Decodes given JWT token and returns its payload's content as {@link JWTPayload} object.
     *
     * @param token given (raw) token
     * @return {@link JWTPayload} object on success with the contents of JWT payload section
     * @throws InvalidJWTTokenException if format of given token is invalid
     */
    @Override
    public JWTPayload decode(String token) throws InvalidJWTTokenException {

        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

            return JWTPayload.getBuilder()
                    .withUsername(claims.get(JWT_USERNAME, String.class))
                    .withExpires(claims.getExpiration())
                    .withIssuedAt(claims.getIssuedAt())
                    .withRole(Role.valueOf(claims.get(JWT_USER_ROLE, String.class)))
                    .withName(claims.get(JWT_USER_PUBLIC_NAME, String.class))
                    .withId(claims.get(JWT_USER_ID, Integer.class))
                    .build();

        } catch(Exception exc) {
            LOGGER.warn(TOKEN_CAN_NOT_BE_DECODED, exc);
            throw new InvalidJWTTokenException(exc);
        }
    }

    /**
     * Extracts token from servlet request. Requires Bearer type Authorization header.
     *
     * @param request standard {@link HttpServletRequest}
     * @return on success, extracted token will be returned as string
     */
    @Override
    public String extractToken(HttpServletRequest request) {

        String authHeader = request.getHeader(AUTH_HEADER);

        String extractedToken = null;
        if(Objects.nonNull(authHeader) && authHeader.startsWith(AUTH_BEARER)) {
            extractedToken = authHeader.substring(AUTH_BEARER.length());
        }

        return extractedToken;
    }

    /**
     * Generates Expires ("exp") value.
     *
     * @param issuedAt date when the token was issued
     * @param expiration expiration in hours
     * @return {@link Long} timestamp of expiration date in seconds
     */
    private Date generateExpiration(Date issuedAt, Integer expiration) {

        Calendar calendar = new Calendar.Builder()
                .setInstant(issuedAt)
                .build();

        calendar.add(Calendar.HOUR, expiration);

        return calendar.getTime();
    }
}
