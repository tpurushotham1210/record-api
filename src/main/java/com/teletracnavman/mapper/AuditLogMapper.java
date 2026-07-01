package com.teletracnavman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.teletracnavman.entity.AuditLog;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestTime", ignore = true)
    @Mapping(target = "httpMethod", constant = "POST")
    AuditLog toEntity(String requestPath, String requestPayload, int responseStatus, Long recordId);
}
