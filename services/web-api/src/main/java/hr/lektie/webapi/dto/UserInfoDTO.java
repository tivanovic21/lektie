package hr.lektie.webapi.dto;

import hr.lektie.common.user.Role;
import hr.lektie.common.user.User;
import java.util.UUID;

/** Public profile of the signed-in user. Never contains the password hash. */
public record UserInfoDTO(UUID id, String username, String displayName, Role role) {

    public static UserInfoDTO from(User user) {
        return new UserInfoDTO(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
    }
}
