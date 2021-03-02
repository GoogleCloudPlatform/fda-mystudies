/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies;

import static org.junit.Assert.assertTrue;

import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import java.io.File;
import org.junit.jupiter.api.Test;

class SwaggerGeneratorTest extends BaseMockIT {

  @Test
  public void createApiDocs() throws Exception {
    String documentPath = generateApiDocs();
    assertTrue(new File(documentPath).exists());
  }
}
