package com.example.backendforcharts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BackendforChartsApplicationTests {

    @Test
    void contextLoads() {
    }
    @Autowired
    JobService jobService;

    @Test
    public void testFileImport() {
        MockMultipartFile file
                = new MockMultipartFile(
                "data",
                "filename.xlsx",
                "text/plain",
                "some data".getBytes()
        );
        Job job = jobService.processFileImport(file);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertEquals("DONE", jobService.getJobStatus(job.getId())));
    }


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    SectionRepository sectionRepository;


    private String token;



    @Test
    void testGetAllSections() throws Exception {
        // Set up user
        User user = (User) User.withDefaultPasswordEncoder()
                .username("root")
                .password("Tapsako@12@")
                .roles("ROOT")
                .build();

        // Authenticate the user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        String token = Base64.getEncoder().encodeToString(("testuser:password").getBytes());

        // Your MockMvc request and assertions

        mockMvc.perform(get("/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + token)  // <-- This line
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
