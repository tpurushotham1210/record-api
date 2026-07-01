package com.teletracnavman.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teletracnavman.dto.RecordPayload;
import com.teletracnavman.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RecordAuditAspect {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public RecordAuditAspect(AuditService auditService, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.teletracnavman.audit.Audited)")
    public Object auditControllerRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = currentRequest();
        RecordPayload payload = extractPayload(joinPoint.getArgs());
        String requestPath = request.getRequestURI();
        String payloadJson = serializePayload(payload);

        try {
            Object result = joinPoint.proceed();
            int responseStatus = extractStatus(result);
            auditService.logRequest(requestPath, payloadJson, responseStatus, AuditContext.getRecordId());
            return result;
        } finally {
            AuditContext.clear();
        }
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No HTTP request available for audit");
        }
        return attributes.getRequest();
    }

    private RecordPayload extractPayload(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof RecordPayload recordPayload) {
                return recordPayload;
            }
        }
        throw new IllegalStateException("Audited endpoint must receive RecordPayload");
    }

    private int extractStatus(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getStatusCode().value();
        }
        throw new IllegalStateException("Audited endpoint must return ResponseEntity");
    }

    private String serializePayload(RecordPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return payload.toString();
        }
    }
}
