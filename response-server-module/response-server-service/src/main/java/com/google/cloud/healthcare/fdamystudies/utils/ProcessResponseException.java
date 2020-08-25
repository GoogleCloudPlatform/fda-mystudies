/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

public class ProcessResponseException extends Exception {

  private static final long serialVersionUID = 1L;

  public ProcessResponseException(String message) {
    super(message);
  }
}
