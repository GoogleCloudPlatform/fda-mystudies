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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
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

  private String siteId;

  private String customLocationId;

  private String locationName;

  private Integer locationStatus;

  private Integer sitePermission;

  private Integer siteStatus;

  private List<ParticipantDetail> registryParticipants = new ArrayList<>();

  private Map<String, Long> countByStatus;

  public ParticipantRegistryDetail(ErrorCode errorCode) {
    super(errorCode);
  }
}
