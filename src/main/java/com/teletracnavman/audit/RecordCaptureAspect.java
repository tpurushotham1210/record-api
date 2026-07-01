package com.teletracnavman.audit;

import com.teletracnavman.entity.RecordEntity;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RecordCaptureAspect {

    @AfterReturning(
            pointcut = "execution(* com.teletracnavman.service.impl.RecordServiceImpl.saveRecord(..))",
            returning = "entity"
    )
    public void captureRecordId(RecordEntity entity) {
        AuditContext.setRecordId(entity.getId());
    }
}
