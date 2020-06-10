/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.exception;

public class SystemException extends RuntimeException {

  private static final long serialVersionUID = -1281191007727915351L;

  public SystemException() {
    super();
  }

  public SystemException(Throwable cause) {
    super(cause);
  }
}
