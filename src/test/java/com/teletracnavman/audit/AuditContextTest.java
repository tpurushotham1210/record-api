package com.teletracnavman.audit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AuditContextTest {

    @AfterEach
    void tearDown() {
        AuditContext.clear();
    }

    @Test
    void setRecordId_storesValueForCurrentThread() {
        AuditContext.setRecordId(42L);

        assertThat(AuditContext.getRecordId()).isEqualTo(42L);
    }

    @Test
    void clear_removesStoredRecordId() {
        AuditContext.setRecordId(42L);

        AuditContext.clear();

        assertThat(AuditContext.getRecordId()).isNull();
    }
}
