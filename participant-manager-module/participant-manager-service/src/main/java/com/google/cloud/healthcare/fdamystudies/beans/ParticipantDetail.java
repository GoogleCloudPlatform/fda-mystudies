/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@JsonInclude(Include.NON_NULL)
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

  private String customLocationId;

  private String locationName;

  private String userDetailsId;

  private String registrationStatus;

  private String studiesEnrolled;

  private String registrationDate;

  private List<AppStudyDetails> enrolledStudies = new ArrayList<>();
}
