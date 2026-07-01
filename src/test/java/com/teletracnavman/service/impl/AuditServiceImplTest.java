package com.teletracnavman.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teletracnavman.entity.AuditLog;
import com.teletracnavman.mapper.AuditLogMapper;
import com.teletracnavman.repository.AuditLogRepository;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Test
    void logRequest_mapsAndPersistsAuditLog() {
        AuditLog auditLog = new AuditLog();
        when(auditLogMapper.toEntity("/echo", "{\"DeviceId\":\"1\"}", 200, 42L))
                .thenReturn(auditLog);

        auditService.logRequest("/echo", "{\"DeviceId\":\"1\"}", 200, 42L);

        verify(auditLogMapper).toEntity("/echo", "{\"DeviceId\":\"1\"}", 200, 42L);
        verify(auditLogRepository).save(auditLog);
    }
}
