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

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EnableDisableParticipantBean {
  private List<Integer> id;

  private String status;

  private ErrorBean errorBean;

  private SuccessBean successBean;
}
