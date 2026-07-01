package com.teletracnavman.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.teletracnavman.dto.DeviceIdResponse;
import com.teletracnavman.dto.RecordPayload;
import com.teletracnavman.entity.RecordEntity;
import com.teletracnavman.service.RecordService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class RecordControllerTest {

    @Mock
    private RecordService recordService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private RecordController recordController;

    private RecordPayload payload;

    @BeforeEach
    void setUp() {
        payload = new RecordPayload(
                "xxx",
                "357370040159770",
                Instant.parse("2014-05-12T05:09:48Z"),
                68,
                "xxx",
                123.45);
    }

    @Test
    void echo_returnsOkWithOriginalPayload() {
        when(recordService.saveRecord(payload)).thenReturn(new RecordEntity());

        ResponseEntity<RecordPayload> response = recordController.echo(payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(payload);
        verify(recordService).saveRecord(payload);
    }

    @Test
    void device_returnsOnlyDeviceId() {
        when(recordService.saveRecord(payload)).thenReturn(new RecordEntity());

        ResponseEntity<DeviceIdResponse> response = recordController.device(payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new DeviceIdResponse("357370040159770"));
        verify(recordService).saveRecord(payload);
    }

    @Test
    void noContent_returnsNoContentStatus() {
        when(recordService.saveRecord(payload)).thenReturn(new RecordEntity());

        ResponseEntity<Void> response = recordController.noContent(payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(recordService).saveRecord(payload);
    }

    @Test
    void nestedNoContent_returnsNoContentWhenPathEndsWithNocontent() {
        when(recordService.saveRecord(payload)).thenReturn(new RecordEntity());
        when(request.getRequestURI()).thenReturn("/api/v1/nocontent");

        ResponseEntity<Void> response = recordController.nestedNoContent(payload, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(recordService).saveRecord(payload);
    }

    @Test
    void nestedNoContent_returnsBadRequestWhenPathDoesNotEndWithNocontent() {
        when(request.getRequestURI()).thenReturn("/unknown");

        ResponseEntity<Void> response = recordController.nestedNoContent(payload, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        verify(recordService, never()).saveRecord(any());
    }
}
