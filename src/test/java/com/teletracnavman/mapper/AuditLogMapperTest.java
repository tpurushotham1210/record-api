package com.teletracnavman.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.teletracnavman.entity.AuditLog;

class AuditLogMapperTest {

    private final AuditLogMapper auditLogMapper = Mappers.getMapper(AuditLogMapper.class);

    @Test
    void toEntity_mapsRequestFieldsAndSetsHttpMethod() {
        AuditLog auditLog = auditLogMapper.toEntity("/echo", "{\"DeviceId\":\"1\"}", 200, 99L);

        assertThat(auditLog.getId()).isNull();
        assertThat(auditLog.getRequestTime()).isNull();
        assertThat(auditLog.getRequestPath()).isEqualTo("/echo");
        assertThat(auditLog.getRequestPayload()).isEqualTo("{\"DeviceId\":\"1\"}");
        assertThat(auditLog.getResponseStatus()).isEqualTo(200);
        assertThat(auditLog.getRecordId()).isEqualTo(99L);
        assertThat(auditLog.getHttpMethod()).isEqualTo("POST");
    }
}
