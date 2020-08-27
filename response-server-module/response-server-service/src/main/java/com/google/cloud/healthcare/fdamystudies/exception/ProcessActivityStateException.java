/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.exception;

public class ProcessActivityStateException extends Exception {

  private static final long serialVersionUID = -6158756148390030536L;

  public ProcessActivityStateException(String message) {
    super(message);
  }
}
