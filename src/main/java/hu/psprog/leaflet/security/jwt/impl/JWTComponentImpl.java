package hu.psprog.leaflet.security.jwt.impl;

import hu.psprog.leaflet.security.jwt.JWTComponent;
import hu.psprog.leaflet.security.jwt.exception.InvalidAuthorizationHeaderException;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT encoder/decoder utility.
 *
 * Application specific JWT secret and expiration shall be provided in external application configuration file.
 */
@Component
public class JWTComponentImpl implements JWTComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTComponentImpl.class);

    private static final String JWT_ISSUED_AT = "iat";
    private static final String JWT_EXPIRES = "exp";
    private static final String AUTH_ERROR_SCHEMA = "Authorization schema must be 'Bearer'";
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
     * Generates token from {@link UserDetails} object.
     *
     * @param userDetails {@link UserDetails} object to generate token based on
     * @return token wrapped in {@link JWTAuthenticationAnswerModel} object
     */
    @Override
    public JWTAuthenticationAnswerModel generateToken(UserDetails userDetails) {

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put(JWT_USERNAME, userDetails.getUsername());
        claims.put(JWT_USER_ROLE, roles);
        claims.put(JWT_EXPIRES, generateExpiration());
        claims.put(JWT_ISSUED_AT, generateIssuedAt());
        claims.put(JWT_USER_PUBLIC_NAME, ((ExtendedUserDetails) userDetails).getName());
        claims.put(JWT_USER_ID, ((ExtendedUserDetails) userDetails).getId().intValue());

        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return new JWTAuthenticationAnswerModel(token);
    }

    /**
     * Decodes given JWT token and returns its payload's content as {@link JWTPayload} object.
     *
     * @param token given (raw) token
     * @return {@link JWTPayload} object on success with the contents of JWT payload section
     * @throws InvalidJWTTokenException
     */
    @Override
    public JWTPayload decode(String token) throws InvalidJWTTokenException {

        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

            JWTPayload payload = new JWTPayload();
            payload.setUsername(claims.get(JWT_USERNAME, String.class));
            payload.setExpires(claims.getExpiration());
            payload.setIssuedAt(claims.getIssuedAt());
            payload.setRole(Role.valueOf(claims.get(JWT_USER_ROLE, String.class)));
            payload.setName(claims.get(JWT_USER_PUBLIC_NAME, String.class));
            payload.setId(claims.get(JWT_USER_ID, Integer.class));

            return payload;

        } catch(Exception exc) {
            LOGGER.warn(TOKEN_CAN_NOT_BE_DECODED, exc);
            throw new InvalidJWTTokenException(exc);
        }
    }

    /**
     * Extracts token from servlet request. Requires Bearer type Authorization header.
     * If Authorization and/or Bearer not found, {@link InvalidAuthorizationHeaderException} will be thrown.
     *
     * @param request standard {@link HttpServletRequest}
     * @return on success, extracted token will be returned as string
     * @throws InvalidAuthorizationHeaderException
     */
    @Override
    public String extractToken(HttpServletRequest request) throws InvalidAuthorizationHeaderException {

        String authHeader = request.getHeader(AUTH_HEADER);

        if(authHeader == null) {
            return null;
        }

        if (!authHeader.startsWith(AUTH_BEARER)) {
            throw new InvalidAuthorizationHeaderException(AUTH_ERROR_SCHEMA);
        }

        return authHeader.substring(AUTH_BEARER.length());
    }

    /**
     * Generates Issued At ("iat") value.
     *
     * @return {@link Long} timestamp of current date in seconds
     */
    private Long generateIssuedAt() {

        Date currentDate = new Date();

        return currentDate.getTime() / 1000;
    }

    /**
     * Generates Expires ("exp") value.
     *
     * @return {@link Long} timestamp of expiration date in seconds
     */
    private Long generateExpiration() {

        long timestamp = generateIssuedAt();
        timestamp += (3600 * expirationInHours);

        return timestamp;
    }
}
