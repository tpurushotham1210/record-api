package com.teletracnavman.service;

import com.teletracnavman.dto.RecordPayload;
import com.teletracnavman.entity.RecordEntity;

public interface RecordService {

    RecordEntity saveRecord(RecordPayload payload);
}
