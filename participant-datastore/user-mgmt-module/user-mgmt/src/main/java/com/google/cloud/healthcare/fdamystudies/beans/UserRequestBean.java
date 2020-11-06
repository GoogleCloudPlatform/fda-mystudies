/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserRequestBean implements Serializable {
  private static final long serialVersionUID = 1L;
  private ProfileRespBean profile;
  private SettingsRespBean settings;
  private InfoBean info;
  private List<ParticipantInfoBean> participantInfo;

  public UserRequestBean(
      ProfileRespBean profile,
      SettingsRespBean settings,
      InfoBean info,
      List<ParticipantInfoBean> participantInfo) {
    super();
    this.profile = profile;
    this.settings = settings;
    this.info = info;
    this.participantInfo = participantInfo;
  }

  public UserRequestBean(SettingsRespBean settings, InfoBean info) {
    super();
    this.settings = settings;
    this.info = info;
  }
}
