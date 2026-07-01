package com.teletracnavman.audit;

public final class AuditContext {

    private static final ThreadLocal<Long> RECORD_ID = new ThreadLocal<>();

    private AuditContext() {
    }

    public static void setRecordId(Long recordId) {
        RECORD_ID.set(recordId);
    }

    public static Long getRecordId() {
        return RECORD_ID.get();
    }

    public static void clear() {
        RECORD_ID.remove();
    }
}
