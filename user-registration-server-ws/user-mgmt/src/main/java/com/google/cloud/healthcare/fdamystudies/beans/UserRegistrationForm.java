/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
/** */
package com.google.cloud.healthcare.fdamystudies.beans;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * PROJECT NAME: FDA-EA-UserRegistration-Server
 *
 * @author Chiranjibi Dash DATE: Dec 13, 2019 TIME: 11:59:07 AM
 */
@Setter
@Getter
@ToString
public class UserRegistrationForm {

  private String userId;
  private String firstName;
  private String lastName;
  private String emailId;
  private String password;
  private boolean usePassCode;
  private boolean touchId;
  private boolean localNotification;
  private boolean remoteNotification;
  private boolean reminderFlag;
  private String auth;
  private int status;
  private boolean tempPassword;
  private LocalDateTime tempPasswordDate;
}
