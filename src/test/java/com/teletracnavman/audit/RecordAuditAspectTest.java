package com.teletracnavman.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teletracnavman.dto.RecordPayload;
import com.teletracnavman.service.AuditService;

@ExtendWith(MockitoExtension.class)
class RecordAuditAspectTest {

    @Mock
    private AuditService auditService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private RecordAuditAspect aspect;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private RecordPayload payload;

    @BeforeEach
    void setUp() throws Throwable {
        aspect = new RecordAuditAspect(auditService, objectMapper);
        payload = new RecordPayload(
                "xxx",
                "357370040159770",
                Instant.parse("2014-05-12T05:09:48Z"),
                68,
                "xxx",
                123.45);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/echo");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(joinPoint.getArgs()).thenReturn(new Object[] {payload});
        when(joinPoint.proceed()).thenReturn(ResponseEntity.ok(payload));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
        AuditContext.clear();
    }

    @Test
    void auditControllerRequest_logsSuccessfulResponseAndClearsContext() throws Throwable {
        AuditContext.setRecordId(15L);

        Object result = aspect.auditControllerRequest(joinPoint);

        assertThat(result).isEqualTo(ResponseEntity.ok(payload));
        verify(auditService).logRequest(
                eq("/echo"),
                eq(objectMapper.writeValueAsString(payload)),
                eq(200),
                eq(15L));
        assertThat(AuditContext.getRecordId()).isNull();
    }

    @Test
    void auditControllerRequest_clearsContextWhenProceedThrows() throws Throwable {
        when(joinPoint.proceed()).thenThrow(new RuntimeException("failure"));

        assertThatThrownBy(() -> aspect.auditControllerRequest(joinPoint))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("failure");
        assertThat(AuditContext.getRecordId()).isNull();
    }
}
