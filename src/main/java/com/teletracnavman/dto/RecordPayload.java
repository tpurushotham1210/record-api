package com.teletracnavman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record RecordPayload(
        @NotBlank(message = "RecordType is mandatory")
        @JsonProperty("RecordType")
        String recordType,

        @NotBlank(message = "DeviceId is mandatory")
        @JsonProperty("DeviceId")
        String deviceId,

        @NotNull(message = "EventDateTime is mandatory")
        @JsonProperty("EventDateTime")
        Instant eventDateTime,

        @JsonProperty("FieldA")
        Integer fieldA,

        @JsonProperty("FieldB")
        String fieldB,

        @JsonProperty("FieldC")
        Double fieldC
) {
}
