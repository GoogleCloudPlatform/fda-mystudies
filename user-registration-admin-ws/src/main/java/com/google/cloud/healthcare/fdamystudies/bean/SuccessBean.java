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

public class SuccessBean {

  public static final String DECOMMISSION_SUCCESS = "decommission successful";

  public static final String REACTIVE_SUCCESS = "reactivate successful";

  public static final String ADD_PARTICIPANT_SUCCESS = "participant added successfully";

  public static final String IMPORT_PARTICIPANT_SUCCESS = "participants imported successfully";

  public static final String PARTICIPANT_ENABLED = "participants are enabled";

  public static final String PARTICIPANT_DISABLED = "participants are disabled";

  public static final String PARTICIPANTS_INVITED = "participants are invited";

  private String message;

  private int code;

  public SuccessBean() {
    this.code = 200;
  }

  public SuccessBean(String message) {
    this.code = 200;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
}
