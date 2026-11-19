package hr.lektie.webapi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hr.lektie.webapi.dto.ResponseObj;
import jakarta.servlet.http.Cookie;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Runs the whole app on the h2 profile with the demo users. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthIntegrationTest.RoleProbeController.class)
class AuthIntegrationTest {

    /** Test-only endpoints that show how role checks are written in real controllers. */
    @RestController
    static class RoleProbeController {

        @GetMapping("/api/test/teacher-only")
        @PreAuthorize("hasRole('TEACHER')")
        ResponseObj<Void> teacherOnly() {
            return new ResponseObj<>("teacher ok");
        }

        @GetMapping("/api/test/staff")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        ResponseObj<Void> staff() {
            return new ResponseObj<>("staff ok");
        }

        @GetMapping("/api/admin/test")
        ResponseObj<Void> admin() {
            return new ResponseObj<>("admin ok");
        }
    }

    @Autowired
    private MockMvc mvc;

    private MvcResult login(String login, String password) throws Exception {
        return mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"" + login + "\",\"password\":\"" + password + "\"}"))
                .andReturn();
    }

    private Cookie tokenCookie(String login, String password) throws Exception {
        Cookie cookie = login(login, password).getResponse().getCookie("token");
        assertThat(cookie).isNotNull();
        return cookie;
    }

    @Test
    void loginSetsHttpOnlyCookieAndReturnsUser() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"Teacher\",\"password\":\"teacher\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().httpOnly("token", true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.username").value("Teacher"))
                .andExpect(jsonPath("$.data.role").value("TEACHER"))
                .andExpect(jsonPath("$.data.passwordHash").doesNotExist());
    }

    @Test
    void usernameIsCaseInsensitivePasswordIsNot() throws Exception {
        assertThat(login("aDmIn", "admin").getResponse().getStatus()).isEqualTo(200);
        assertThat(login("admin", "ADMIN").getResponse().getStatus()).isEqualTo(401);
    }

    @Test
    void wrongCredentialsGive401WithSameMessage() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"nobody\",\"password\":\"x\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"Student\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void blankFieldsGive400() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void jwtCarriesRole() throws Exception {
        String token = tokenCookie("Parent", "parent").getValue();
        String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
        assertThat(payload).contains("\"role\":\"PARENT\"").contains("\"username\":\"Parent\"");
    }

    @Test
    void meWorksWithCookieAndWithBearer() throws Exception {
        Cookie cookie = tokenCookie("Student", "student");
        mvc.perform(get("/api/auth/me").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + cookie.getValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("Student"));
    }

    @Test
    void missingOrBadTokenGives401() throws Exception {
        mvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication required"));
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer not.a.jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void roleChecksOnMethodsAndUrls() throws Exception {
        Cookie teacher = tokenCookie("Teacher", "teacher");
        Cookie student = tokenCookie("Student", "student");
        Cookie admin = tokenCookie("Admin", "admin");

        mvc.perform(get("/api/test/teacher-only").cookie(teacher)).andExpect(status().isOk());
        mvc.perform(get("/api/test/teacher-only").cookie(student))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));

        mvc.perform(get("/api/test/staff").cookie(admin)).andExpect(status().isOk());
        mvc.perform(get("/api/test/staff").cookie(student)).andExpect(status().isForbidden());

        mvc.perform(get("/api/admin/test").cookie(admin)).andExpect(status().isOk());
        mvc.perform(get("/api/admin/test").cookie(teacher)).andExpect(status().isForbidden());
    }

    @Test
    void logoutClearsCookie() throws Exception {
        mvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("token", 0));
    }

    @Test
    void pingIsPublic() throws Exception {
        mvc.perform(get("/api/ping")).andExpect(status().isOk());
    }
}
