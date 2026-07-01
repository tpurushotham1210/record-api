package com.teletracnavman.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.teletracnavman.controller.RecordController;
import com.teletracnavman.dto.RecordPayload;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationErrors_returnsBadRequestWithJsonFieldNames() throws Exception {
        RecordPayload payload = new RecordPayload("", "357370040159770", null, null, null, null);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(payload, "payload");
        bindingResult.addError(new FieldError("payload", "recordType", "RecordType is mandatory"));
        bindingResult.addError(new FieldError("payload", "eventDateTime", "EventDateTime is mandatory"));

        Method method = RecordController.class.getDeclaredMethod("echo", RecordPayload.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Validation failed");
        @SuppressWarnings("unchecked")
        Map<String, String> fields = (Map<String, String>) response.getBody().get("fields");
        assertThat(fields)
                .containsEntry("RecordType", "RecordType is mandatory")
                .containsEntry("EventDateTime", "EventDateTime is mandatory");
    }
}
