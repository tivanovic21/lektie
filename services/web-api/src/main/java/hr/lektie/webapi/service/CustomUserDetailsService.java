package hr.lektie.webapi.service;

import hr.lektie.common.user.User;
import hr.lektie.webapi.repository.UserRepository;
import hr.lektie.webapi.security.LektieUserDetails;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Finds the account for the "login" field of the login form. Spring Security then checks the password. */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return findByLogin(login)
                .map(LektieUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Username lookup, case-insensitive.
     * Email login goes here later: once users has an email column, check login.contains("@")
     * and look the user up by email instead.
     */
    private Optional<User> findByLogin(String login) {
        if (login == null || login.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByUsernameIgnoreCase(login.trim());
    }
}
