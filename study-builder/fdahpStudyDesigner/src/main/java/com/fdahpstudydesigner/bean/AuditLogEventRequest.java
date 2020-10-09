/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditLogEventRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private String correlationId;

  private String eventCode;

  private String description;

  private String source;

  private String destination;

  private String resourceServer;

  private String userId;

  private String userAccessLevel;

  private String sourceApplicationVersion;

  private String destinationApplicationVersion;

  private String platformVersion;

  private Timestamp occured;

  private String appId;

  private String userIp;

  private String mobilePlatform;

  private String appVersion;

  private String participantId;

  private String studyId;

  private String studyVersion;

  private String siteId;
}
