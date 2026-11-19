package hr.lektie.webapi.ping;

import hr.lektie.webapi.dto.ResponseObj;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Smoke endpoint so clients can check that the API is up. Public. */
@RestController
public class PingController {

    @GetMapping("/api/ping")
    public ResponseObj<Map<String, String>> ping() {
        return new ResponseObj<>("OK", Map.of("service", "web-api", "status", "ok"));
    }
}
