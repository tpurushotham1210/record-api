package com.teletracnavman.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teletracnavman.dto.RecordPayload;
import com.teletracnavman.entity.RecordEntity;
import com.teletracnavman.repository.RecordRepository;

@ExtendWith(MockitoExtension.class)
class RecordServiceImplTest {

    @Mock
    private RecordRepository recordRepository;

    @InjectMocks
    private RecordServiceImpl recordService;

    @Test
    void saveRecord_copiesPayloadFieldsAndPersistsEntity() {
        RecordPayload payload = samplePayload();
        RecordEntity saved = new RecordEntity();
        saved.setId(1L);
        when(recordRepository.save(any(RecordEntity.class))).thenReturn(saved);

        RecordEntity result = recordService.saveRecord(payload);

        ArgumentCaptor<RecordEntity> captor = ArgumentCaptor.forClass(RecordEntity.class);
        verify(recordRepository).save(captor.capture());

        RecordEntity entity = captor.getValue();
        assertThat(entity.getRecordType()).isEqualTo("xxx");
        assertThat(entity.getDeviceId()).isEqualTo("357370040159770");
        assertThat(entity.getEventDateTime()).isEqualTo(Instant.parse("2014-05-12T05:09:48Z"));
        assertThat(entity.getFieldA()).isEqualTo(68);
        assertThat(entity.getFieldB()).isEqualTo("xxx");
        assertThat(entity.getFieldC()).isEqualTo(123.45);
        assertThat(result).isSameAs(saved);
    }

    private static RecordPayload samplePayload() {
        return new RecordPayload(
                "xxx",
                "357370040159770",
                Instant.parse("2014-05-12T05:09:48Z"),
                68,
                "xxx",
                123.45);
    }
}
