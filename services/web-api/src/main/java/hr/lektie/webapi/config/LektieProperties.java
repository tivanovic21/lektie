package hr.lektie.webapi.config;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Typed view of the "lektie.*" block in application.yml. */
@ConfigurationProperties("lektie")
public record LektieProperties(
        Jwt jwt,
        Cookie cookie,
        List<String> corsAllowedOrigins,
        boolean seedDemoUsers) {

    public record Jwt(
            String privateKey,
            String publicKey,
            String issuer,
            Duration expiration,
            boolean generateKeysIfMissing) {
    }

    public record Cookie(String name, boolean secure, String sameSite) {
    }
}
