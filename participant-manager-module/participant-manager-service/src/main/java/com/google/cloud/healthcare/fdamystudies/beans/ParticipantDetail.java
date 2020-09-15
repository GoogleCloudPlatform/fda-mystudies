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
@NoArgsConstructor
public class ParticipantDetail {

  private String id;

  private String email;

  private String onboardingStatus;

  private String enrollmentStatus;

  private String enrollmentDate;

  private String invitedDate;

  private String siteId;

  private String locationName;

  private String customLocationId;

  private String participantRegistrySiteid;

  private String customStudyId;

  private String studyName;

  private String customAppId;

  private String appName;

  private String onboardringStatus;

  private String invitationDate;

  private String userDetailsId;

  private String registrationStatus;

  private String studiesEnrolled;

  private String registrationDate;

  private List<AppStudyDetails> enrolledStudies = new ArrayList<>();

  private List<Enrollment> enrollments = new ArrayList<>();

  private List<ConsentHistory> consentHistory = new ArrayList<>();

  private Boolean newlyCreatedUser = Boolean.FALSE;
}
