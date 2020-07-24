/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.google.cloud.healthcare.fdamystudies.beans.AppSiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteResponse;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;

public class SiteMapper {

  private SiteMapper() {}

  public static SiteResponse toSiteResponse(SiteEntity site) {
    SiteResponse response = new SiteResponse();
    response.setSiteId(site.getId());
    return response;
  }

  public static List<AppSiteResponse> toAppDetailsResponseList(List<SiteEntity> sites) {
    List<AppSiteResponse> siteResponseList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(sites)) {
      for (SiteEntity site : sites) {
        AppSiteResponse appSiteResponse = new AppSiteResponse();
        appSiteResponse.setSiteId(site.getId());
        appSiteResponse.setCustomLocationId(site.getLocation().getCustomId());
        appSiteResponse.setLocationDescription(site.getLocation().getDescription());
        appSiteResponse.setLocationId(site.getLocation().getId());
        appSiteResponse.setLocationName(site.getLocation().getName());
        siteResponseList.add(appSiteResponse);
      }
    }
    return siteResponseList;
  }
}
