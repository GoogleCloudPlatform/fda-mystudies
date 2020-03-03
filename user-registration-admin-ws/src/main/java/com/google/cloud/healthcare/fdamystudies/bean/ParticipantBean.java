/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@Component
@Scope(value = "prototype")
@ToString
@Getter
@Setter
public class ParticipantBean {

  private Integer id = 0;
  private String email = "";
  private String onboardingStatus = "";
  private String enrollmentStatus = "";
  private String enrollmentDate = "";
  private String invitedDate = "";
  private Integer siteId = 0;
  private String customLocationId = "";
  private String locationName = "";
  private Boolean newlyCreatedUser = null;

  private ErrorBean errorBean;
  private SuccessBean successBean;
}
