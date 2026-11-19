package hr.lektie.webapi.security;

import hr.lektie.common.user.Role;
import java.util.UUID;

/**
 * Who is calling, read from a valid JWT without touching the database.
 * Get it in a controller with {@code @AuthenticationPrincipal AuthUser user}.
 */
public record AuthUser(UUID id, String username, Role role) {
}
