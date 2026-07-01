package com.teletracnavman;

import com.teletracnavman.entity.AuditLog;
import com.teletracnavman.entity.RecordEntity;
import com.teletracnavman.repository.AuditLogRepository;
import com.teletracnavman.repository.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecordControllerIntegrationTest {

    @Value("${api.auth.token}")
    private String authToken;

    private static final String VALID_PAYLOAD = """
            {
              "RecordType": "xxx",
              "DeviceId": "357370040159770",
              "EventDateTime": "2014-05-12T05:09:48Z",
              "FieldA": 68,
              "FieldB": "xxx",
              "FieldC": 123.45
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void cleanDatabase() {
        auditLogRepository.deleteAll();
        recordRepository.deleteAll();
    }

    @Test
    void rejectsRequestWithoutToken() throws Exception {
        mockMvc.perform(post("/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized: invalid or missing token"));
    }

    @Test
    void rejectsRequestWithInvalidToken() throws Exception {
        mockMvc.perform(post("/echo")
                        .header("Authorization", "Bearer wrong-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized: invalid or missing token"));
    }

    @Test
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/echo")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "RecordType": "",
                                  "DeviceId": "357370040159770"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.EventDateTime").exists());
    }

    @Test
    void echoReturnsOriginalPayloadAndPersistsData() throws Exception {
        mockMvc.perform(post("/echo")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.DeviceId").value("357370040159770"))
                .andExpect(jsonPath("$.RecordType").value("xxx"));

        assertThat(recordRepository.count()).isEqualTo(1);
        RecordEntity record = recordRepository.findAll().get(0);
        assertThat(record.getDeviceId()).isEqualTo("357370040159770");

        assertThat(auditLogRepository.count()).isEqualTo(1);
        AuditLog audit = auditLogRepository.findAll().get(0);
        assertThat(audit.getRequestPath()).isEqualTo("/echo");
        assertThat(audit.getResponseStatus()).isEqualTo(200);
    }

    @Test
    void deviceReturnsOnlyDeviceId() throws Exception {
        mockMvc.perform(post("/device")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"DeviceId\":\"357370040159770\"}"));

        assertThat(recordRepository.count()).isEqualTo(1);
        assertThat(auditLogRepository.findAll().get(0).getResponseStatus()).isEqualTo(200);
    }

    @Test
    void exactNoContentPathReturns204() throws Exception {
        mockMvc.perform(post("/nocontent")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isNoContent());

        assertThat(recordRepository.count()).isEqualTo(1);
        assertThat(auditLogRepository.findAll().get(0).getResponseStatus()).isEqualTo(204);
    }

    @Test
    void nestedNoContentPathReturns204() throws Exception {
        mockMvc.perform(post("/api/v1/nocontent")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isNoContent());

        assertThat(recordRepository.count()).isEqualTo(1);
        assertThat(auditLogRepository.findAll().get(0).getResponseStatus()).isEqualTo(204);
    }

    @Test
    void unknownPathReturns400WithValidToken() throws Exception {
        mockMvc.perform(post("/unknown")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));

        assertThat(recordRepository.count()).isZero();
        assertThat(auditLogRepository.count()).isZero();
    }

    @Test
    void unknownPathReturns400WithoutToken() throws Exception {
        mockMvc.perform(post("/unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));

        assertThat(recordRepository.count()).isZero();
        assertThat(auditLogRepository.count()).isZero();
    }

    @Test
    void pathContainingNocontentSubstringReturns400() throws Exception {
        mockMvc.perform(post("/not-nocontent")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));

        assertThat(recordRepository.count()).isZero();
        assertThat(auditLogRepository.count()).isZero();
    }

    @Test
    void getOnAllowedPathReturns400WithoutToken() throws Exception {
        mockMvc.perform(get("/echo"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));

        assertThat(recordRepository.count()).isZero();
        assertThat(auditLogRepository.count()).isZero();
    }

    @Test
    void getOnAllowedPathReturns400WithValidToken() throws Exception {
        mockMvc.perform(get("/echo")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));

        assertThat(recordRepository.count()).isZero();
        assertThat(auditLogRepository.count()).isZero();
    }

    @Test
    void getOnUnknownPathReturns400WithValidToken() throws Exception {
        mockMvc.perform(get("/unknown")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));

        assertThat(recordRepository.count()).isZero();
        assertThat(auditLogRepository.count()).isZero();
    }
}
