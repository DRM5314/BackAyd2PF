package library.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@AutoConfigureMockMvc
@WebMvcTest
public class AbstractMvcTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public MockMvcBuilderCustomizer defaultMockMvcBuilderCustomizer() {
            return builder -> {
                builder.defaultRequest(delete("")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON));
                builder.alwaysDo(print());
            };
        }
    }
}
