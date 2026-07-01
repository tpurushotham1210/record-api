package com.teletracnavman.service;

public interface AuditService {

    void logRequest(String requestPath, String requestPayload, int responseStatus, Long recordId);
}
