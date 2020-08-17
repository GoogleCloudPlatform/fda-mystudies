/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.AppDetails;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;

public final class AppMapper {
  private AppMapper() {}

  public static AppDetails toAppDetails(AppEntity app) {
    AppDetails appDetails = new AppDetails();
    appDetails.setId(app.getId());
    appDetails.setCustomId(app.getAppId());
    appDetails.setName(app.getAppName());
    return appDetails;
  }
}
