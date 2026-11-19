package hr.lektie.webapi.config;

import hr.lektie.common.user.Role;
import hr.lektie.common.user.User;
import hr.lektie.webapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Demo accounts for development (lektie.seed-demo-users=true, on by default in the h2 profile). */
@Configuration
@ConditionalOnBooleanProperty("lektie.seed-demo-users")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private record DemoUser(String username, String password, Role role) {
    }

    private static final DemoUser[] DEMO_USERS = {
            new DemoUser("Admin", "admin", Role.ADMIN),
            new DemoUser("Parent", "parent", Role.PARENT),
            new DemoUser("Teacher", "teacher", Role.TEACHER),
            new DemoUser("Student", "student", Role.STUDENT),
    };

    @Bean
    public ApplicationRunner seedDemoUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            int created = 0;
            for (DemoUser demo : DEMO_USERS) {
                if (!userRepository.existsByUsernameIgnoreCase(demo.username())) {
                    userRepository.save(new User(demo.username(), passwordEncoder.encode(demo.password()),
                            demo.username(), demo.role()));
                    created++;
                }
            }
            log.info("Demo users: created={}, already present={}", created, DEMO_USERS.length - created);
        };
    }
}
