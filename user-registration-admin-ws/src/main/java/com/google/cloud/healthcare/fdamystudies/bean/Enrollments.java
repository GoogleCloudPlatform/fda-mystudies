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

public class Enrollments {
  private String participantId;

  private String withdrawalDate;

  private String enrollmentStatus;

  private String enrollmentDate;

  public String getParticipantId() {
    return participantId;
  }

  public void setParticipantId(String participantId) {
    this.participantId = participantId;
  }

  public String getWithdrawalDate() {
    return withdrawalDate;
  }

  public void setWithdrawalDate(String withdrawalDate) {
    this.withdrawalDate = withdrawalDate;
  }

  public String getEnrollmentStatus() {
    return enrollmentStatus;
  }

  public void setEnrollmentStatus(String enrollmentStatus) {
    this.enrollmentStatus = enrollmentStatus;
  }

  public String getEnrollmentDate() {
    return enrollmentDate;
  }

  public void setEnrollmentDate(String enrollmentDate) {
    this.enrollmentDate = enrollmentDate;
  }
}
