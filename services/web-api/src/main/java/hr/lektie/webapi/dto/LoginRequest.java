package hr.lektie.webapi.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Login body. "login" is a username for now (case-insensitive); it is named generically so an email
 * can be accepted in the same field later without changing the API (see LoginLookupService).
 */
public record LoginRequest(@NotBlank String login, @NotBlank String password) {
}
