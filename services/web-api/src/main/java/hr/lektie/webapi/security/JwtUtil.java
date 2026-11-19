package hr.lektie.webapi.security;

import hr.lektie.common.user.Role;
import hr.lektie.webapi.config.LektieProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Issues and verifies RS256 JWTs.
 * Claims: sub = user id, "username", "role" (e.g. "TEACHER"), iss, iat, exp.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final String USERNAME_CLAIM = "username";
    private static final String ROLE_CLAIM = "role";

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String issuer;
    private final Duration expiration;

    public JwtUtil(LektieProperties properties) {
        LektieProperties.Jwt jwt = properties.jwt();
        this.issuer = jwt.issuer();
        this.expiration = jwt.expiration();

        boolean hasPrivate = jwt.privateKey() != null && !jwt.privateKey().isBlank();
        boolean hasPublic = jwt.publicKey() != null && !jwt.publicKey().isBlank();
        try {
            if (hasPrivate && hasPublic) {
                this.privateKey = loadPrivateKey(jwt.privateKey());
                this.publicKey = loadPublicKey(jwt.publicKey());
            } else if (!hasPrivate && !hasPublic && jwt.generateKeysIfMissing()) {
                log.warn("JWT_PRIVATE_KEY/JWT_PUBLIC_KEY not set: using a generated key pair. "
                        + "Tokens stop working after a restart. Set the keys outside local development.");
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                KeyPair pair = generator.generateKeyPair();
                this.privateKey = pair.getPrivate();
                this.publicKey = pair.getPublic();
            } else {
                throw new IllegalStateException("Set both JWT_PRIVATE_KEY and JWT_PUBLIC_KEY (see .env.example)");
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Could not load the JWT keys from JWT_PRIVATE_KEY/JWT_PUBLIC_KEY", e);
        }
    }

    public Duration getExpiration() {
        return expiration;
    }

    public String generateToken(UUID userId, String username, Role role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim(USERNAME_CLAIM, username)
                .claim(ROLE_CLAIM, role.name())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    /** Empty if the token is malformed, expired, signed with another key or from another issuer. */
    public Optional<AuthUser> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new AuthUser(
                    UUID.fromString(claims.getSubject()),
                    claims.get(USERNAME_CLAIM, String.class),
                    Role.valueOf(claims.get(ROLE_CLAIM, String.class))));
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            log.debug("Rejected JWT: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static PrivateKey loadPrivateKey(String pem) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(stripPem(pem));
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private static PublicKey loadPublicKey(String pem) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(stripPem(pem));
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }

    /** Accepts a full PEM or only its base64 body, on one line or many (also literal "\n" from .env files). */
    private static String stripPem(String pem) {
        return pem
                .replaceAll("-----(BEGIN|END) [A-Z ]+-----", "")
                .replace("\\n", "")
                .replaceAll("\\s", "");
    }
}
