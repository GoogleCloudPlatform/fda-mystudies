/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.service.AuditEventService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudyBuilderAuditEventHelper {

  @Autowired AuditEventService auditService;

  public void logEvent(StudyBuilderAuditEvent eventEnum, AuditLogEventRequest auditRequest) {
    logEvent(eventEnum, auditRequest, null);
  }

  public void logEvent(
      StudyBuilderAuditEvent eventEnum,
      AuditLogEventRequest auditRequest,
      Map<String, String> values) {
    if (eventEnum != null) {
      String description = eventEnum.getDescription();
      if (values != null) {
        description = PlaceholderReplacer.replaceNamedPlaceholders(description, values);
      }
      auditRequest.setDescription(description);

      auditRequest =
          AuditEventMapper.fromAuditLogEventEnumAndCommonPropConfig(eventEnum, auditRequest);
      auditService.postAuditLogEvent(auditRequest);
    }
  }

  public void logEvent(
      List<StudyBuilderAuditEvent> eventEnum,
      AuditLogEventRequest auditRequest,
      Map<String, String> values) {
    for (int i = 0; i < eventEnum.size(); i++) {
      String description = eventEnum.get(i).getDescription();
      if (values != null) {
        description = PlaceholderReplacer.replaceNamedPlaceholders(description, values);
      }
      auditRequest.setDescription(description);
      auditRequest =
          AuditEventMapper.fromAuditLogEventEnumAndCommonPropConfig(eventEnum.get(i), auditRequest);
      auditService.postAuditLogEvent(auditRequest);
    }
  }
}
