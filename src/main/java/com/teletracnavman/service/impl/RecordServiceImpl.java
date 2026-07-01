package com.teletracnavman.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teletracnavman.dto.RecordPayload;
import com.teletracnavman.entity.RecordEntity;
import com.teletracnavman.repository.RecordRepository;
import com.teletracnavman.service.RecordService;

@Service
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;

    public RecordServiceImpl(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    @Transactional
    public RecordEntity saveRecord(RecordPayload payload) {
        RecordEntity entity = new RecordEntity();
        BeanUtils.copyProperties(payload, entity);
        return recordRepository.save(entity);
    }
}
