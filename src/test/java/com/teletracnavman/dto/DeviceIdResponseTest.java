package com.teletracnavman.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class DeviceIdResponseTest {

    @Test
    void from_extractsDeviceIdFromPayload() {
        RecordPayload payload = new RecordPayload(
                "xxx",
                "357370040159770",
                Instant.parse("2014-05-12T05:09:48Z"),
                null,
                null,
                null);

        DeviceIdResponse response = DeviceIdResponse.from(payload);

        assertThat(response.deviceId()).isEqualTo("357370040159770");
    }
}
