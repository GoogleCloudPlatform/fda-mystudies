/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.config;

import org.hibernate.dialect.H2Dialect;

public class H2DialectExtended extends H2Dialect {

  @Override
  public String toBooleanValueString(boolean bool) {
    return bool ? "TRUE" : "FALSE";
  }
}
