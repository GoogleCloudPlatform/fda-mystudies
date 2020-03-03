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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Project Name: MyStudiesuserRegWs
 *
 * @author Chiranjibi Dash, Date: Dec 23, 2019, Time: 7:21:23 PM
 */
@Setter
@Getter
@ToString
public class VerifyTokenResponseBean {

  private String code;
  private String message;

  private String accessToken;
  private boolean isVerified;
  private String expireDateTime;
}
