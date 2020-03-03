/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.beans;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserRequestBean implements Serializable {
  private static final long serialVersionUID = 1L;
  private ProfileRespBean profile;
  private SettingsRespBean settings;
  private InfoBean info;
  private List<ParticipantInfoBean> participantInfo;
}
