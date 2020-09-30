/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;

public class BadRequestException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public BadRequestException(String message) {
    super(message);
  }
}
