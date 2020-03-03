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
package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Project Name: UserManagementServiceBundle
 *
 * @author Chiranjibi Dash
 */
@ToString
@Setter
@Getter
public class VerifyEmailIdResponse {

  private Integer code;
  private String message;
  private boolean verified;
}
