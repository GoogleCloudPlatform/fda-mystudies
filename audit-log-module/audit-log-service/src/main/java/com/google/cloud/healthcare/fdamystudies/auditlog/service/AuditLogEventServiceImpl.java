package com.google.cloud.healthcare.fdamystudies.auditlog.service;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.auditlog.beans.AuditLogEventResponse;
import com.google.cloud.healthcare.fdamystudies.auditlog.mapper.AuditLogEventMapper;
import com.google.cloud.healthcare.fdamystudies.auditlog.model.AuditLogEventEntity;
import com.google.cloud.healthcare.fdamystudies.auditlog.repository.AuditLogEventRepository;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.service.BaseServiceImpl;

@Service
public class AuditLogEventServiceImpl extends BaseServiceImpl implements AuditLogEventService {

  private XLogger logger = XLoggerFactory.getXLogger(AuditLogEventServiceImpl.class.getName());

  @Autowired private AuditLogEventRepository repository;

  @Value("${auditlog.platform-version}")
  private String platformVersion;

  @Override
  public AuditLogEventResponse saveAuditLogEvent(AuditLogEventRequest aleRequest) {
    logger.entry(String.format("begin saveAuditLogEvent() with aleRequest=%s", aleRequest));
    AuditLogEventEntity aleEntity = AuditLogEventMapper.toAuditLogEventEntity(aleRequest);
    aleEntity.setPlatformVersion(platformVersion);

    aleEntity = repository.saveAndFlush(aleEntity);

    AuditLogEventResponse aleResponse = AuditLogEventMapper.toAuditLogEventResponse(aleEntity);
    logger.exit(String.format("aleResponse=%s", aleResponse));
    return aleResponse;
  }
}
