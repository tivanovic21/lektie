package hr.lektie.webapi.ping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PingControllerTest {

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new PingController()).build();

    @Test
    void pingReturnsOk() throws Exception {
        mvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("web-api"))
                .andExpect(jsonPath("$.status").value("ok"));
    }
}
