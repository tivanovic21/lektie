package hr.lektie.mobileapi.ping;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Smoke endpoint so clients can check that the API is up. */
@RestController
public class PingController {

    @GetMapping("/api/ping")
    public Map<String, String> ping() {
        return Map.of("service", "mobile-api", "status", "ok");
    }
}
