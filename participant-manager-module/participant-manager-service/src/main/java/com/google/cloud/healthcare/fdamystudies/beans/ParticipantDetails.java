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

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ParticipantDetails {

  private String participantRegistrySiteid;

  private String siteId;

  private String customLocationId;

  private String locationName;

  private String customStudyId;

  private String studyName;

  private String customAppId;

  private String appName;

  private String onboardringStatus;

  private String email;

  private String invitationDate;

  private String userDetailsId;

  private String registrationStatus;

  private String studiesEnrolled;

  private String registrationDate;

  private List<AppStudyDetails> enrolledStudies = new ArrayList<>();

  private List<Enrollment> enrollments = new ArrayList<>();

  private List<ConsentHistory> consentHistory = new ArrayList<>();
}
