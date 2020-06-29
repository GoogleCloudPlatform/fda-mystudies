/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.exceptions;

public class OrchestrationException extends CommonException {

  public OrchestrationException(String perrorCode, Throwable arg1) {
    super(perrorCode, arg1);
  }

  private static final long serialVersionUID = 1081061948859074979L;
}
