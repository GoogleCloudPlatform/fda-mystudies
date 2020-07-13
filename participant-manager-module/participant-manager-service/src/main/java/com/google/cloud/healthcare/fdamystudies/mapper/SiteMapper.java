/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.SiteResponse;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;

public class SiteMapper {

  private SiteMapper() {}

  public static SiteResponse toSiteResponse(SiteEntity site) {
    SiteResponse response = new SiteResponse();
    response.setSiteId(site.getId());
    return response;
  }
}
