/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsResponseBean {

  private ErrorBean error = new ErrorBean();

  private ProfileRespBean profileRespBean;
}
