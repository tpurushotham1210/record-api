package com.teletracnavman.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    private static final String VALID_PAYLOAD = """
            {
              "RecordType": "xxx",
              "DeviceId": "357370040159770",
              "EventDateTime": "2014-05-12T05:09:48Z"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Value("${api.auth.token}")
    private String authToken;

    @Test
    void rejectsPostRequestWithoutToken() throws Exception {
        mockMvc.perform(post("/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isEqualTo(401))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);
                    assertThat(response.getContentAsString())
                            .contains("Unauthorized: invalid or missing token");
                });
    }

    @Test
    void rejectsPostRequestWithInvalidToken() throws Exception {
        mockMvc.perform(post("/echo")
                        .header("Authorization", "Bearer wrong-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isEqualTo(401));
    }

    @Test
    void rejectsGetRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/echo"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isEqualTo(400))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());
    }

    @Test
    void rejectsGetRequestWithInvalidToken() throws Exception {
        mockMvc.perform(get("/echo")
                        .header("Authorization", "Bearer wrong-token"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isEqualTo(400))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());
    }

    @Test
    void rejectsGetOnAllowedPathWithValidToken() throws Exception {
        mockMvc.perform(get("/echo")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());
    }

    @Test
    void rejectsGetOnUnknownPathWithValidToken() throws Exception {
        mockMvc.perform(get("/unknown")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());
    }

    @Test
    void allowsKnownPostPathsWithValidToken() throws Exception {
        mockMvc.perform(post("/echo")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isOk());

        mockMvc.perform(post("/device")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isOk());

        mockMvc.perform(post("/nocontent")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/nocontent")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isNoContent());
    }

    @Test
    void rejectsUnknownPostPathWithoutToken() throws Exception {
        mockMvc.perform(post("/unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isEqualTo(400))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());
    }

    @Test
    void rejectsUnknownPostPathWithValidToken() throws Exception {
        mockMvc.perform(post("/unknown")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isEqualTo(400))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());
    }

    @Test
    void rejectsPathContainingNocontentSubstringWithValidToken() throws Exception {
        mockMvc.perform(post("/not-nocontent")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());
    }
}
