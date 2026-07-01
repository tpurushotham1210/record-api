package com.teletracnavman.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teletracnavman.mapper.AuditLogMapper;
import com.teletracnavman.repository.AuditLogRepository;
import com.teletracnavman.service.AuditService;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    public AuditServiceImpl(AuditLogRepository auditLogRepository, AuditLogMapper auditLogMapper) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    @Transactional
    public void logRequest(String requestPath, String requestPayload, int responseStatus, Long recordId) {
        auditLogRepository.save(
                auditLogMapper.toEntity(requestPath, requestPayload, responseStatus, recordId));
    }
}
