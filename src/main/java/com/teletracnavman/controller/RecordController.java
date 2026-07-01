package com.teletracnavman.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.teletracnavman.audit.Audited;
import com.teletracnavman.dto.DeviceIdResponse;
import com.teletracnavman.dto.RecordPayload;
import com.teletracnavman.service.RecordService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/echo")
    @Audited
    public ResponseEntity<RecordPayload> echo(@Valid @RequestBody RecordPayload payload) {
        saveRecord(payload);
        return ResponseEntity.ok(payload);
    }

    @PostMapping("/device")
    @Audited
    public ResponseEntity<DeviceIdResponse> device(@Valid @RequestBody RecordPayload payload) {
        saveRecord(payload);
        return ResponseEntity.ok(DeviceIdResponse.from(payload));
    }

    @PostMapping("/nocontent")
    @Audited
    public ResponseEntity<Void> noContent(@Valid @RequestBody RecordPayload payload) {
        saveRecord(payload);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/**")
    @Audited
    public ResponseEntity<Void> nestedNoContent(@Valid @RequestBody RecordPayload payload,
                                                HttpServletRequest request) {
        if (request.getRequestURI().endsWith("/nocontent")) {
            saveRecord(payload);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    private void saveRecord(RecordPayload payload) {
        recordService.saveRecord(payload);
    }
}
