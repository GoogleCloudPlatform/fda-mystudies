/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.common.JsonUtils.getObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class AuditEventServiceImpl implements AuditEventService {

  private static Logger logger = Logger.getLogger(AuditEventServiceImpl.class);

  @Override
  public void postAuditLogEvent(AuditLogEventRequest auditRequest) {
    logger.debug(
        String.format("begin postAuditLogEvent() for %s event", auditRequest.getEventCode()));

    JsonNode requestBody = getObjectMapper().convertValue(auditRequest, JsonNode.class);

    // TODO (#703) integration with GCP stackdriver. Please remove the requestBody from below logger
    // statement during stackdriver integration as it may contain PII information.
    logger.debug(String.format("audit request=%s", requestBody));
  }
}
