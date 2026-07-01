package com.teletracnavman.audit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.teletracnavman.entity.RecordEntity;

class RecordCaptureAspectTest {

    private final RecordCaptureAspect aspect = new RecordCaptureAspect();

    @AfterEach
    void tearDown() {
        AuditContext.clear();
    }

    @Test
    void captureRecordId_storesEntityIdInAuditContext() {
        RecordEntity entity = new RecordEntity();
        entity.setId(7L);

        aspect.captureRecordId(entity);

        assertThat(AuditContext.getRecordId()).isEqualTo(7L);
    }
}
