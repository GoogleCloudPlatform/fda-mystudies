/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantRegistryDetail extends BaseResponse {

  private String studyId;

  private String customStudyId;

  private String studyType;

  private Integer targetEnrollment;

  private String studyName;

  private String appId;

  private String customAppId;

  private String appName;

  private String appStatus;

  private String siteId;

  private String customLocationId;

  private String locationName;

  private Integer locationStatus;

  private Integer sitePermission;

  private Integer studyPermission;

  private Integer siteStatus;

  private String studyStatus;

  private Integer openStudySitePermission;

  private List<ParticipantDetail> registryParticipants = new ArrayList<>();

  private Map<String, Long> countByStatus;
}
