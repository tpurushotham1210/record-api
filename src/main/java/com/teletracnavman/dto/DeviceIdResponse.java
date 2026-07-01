package com.teletracnavman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeviceIdResponse(
        @JsonProperty("DeviceId")
        String deviceId
) {
    public static DeviceIdResponse from(RecordPayload payload) {
        return new DeviceIdResponse(payload.deviceId());
    }
}
