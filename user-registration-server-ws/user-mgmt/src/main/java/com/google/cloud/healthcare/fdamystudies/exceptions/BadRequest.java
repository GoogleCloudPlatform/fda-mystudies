/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.exceptions;

public class BadRequest extends RuntimeException {

  private static final long serialVersionUID = 4205644147470222834L;

  public BadRequest(String message) {
    super("Bad request: " + message);
  }
}
