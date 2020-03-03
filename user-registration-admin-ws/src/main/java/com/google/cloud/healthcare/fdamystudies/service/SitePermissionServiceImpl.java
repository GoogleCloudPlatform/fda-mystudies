/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.dao.SitePermissionDAO;
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;

@Service
public class SitePermissionServiceImpl implements SitePermissionService {

  @Autowired SitePermissionDAO sitePermissionDAO;
  private static final Logger logger = LoggerFactory.getLogger(SitePermissionDAO.class);

  @Override
  public SitePermission getSitePermissionForUser(Integer siteId, Integer userId) {
    logger.info("SitePermissionServiceImpl - getSitePermissionForUser - starts");
    SitePermission sitePermission = null;
    try {
      sitePermission = sitePermissionDAO.getSitePermissionForUser(siteId, userId);
    } catch (Exception e) {
      logger.error("SitePermissionServiceImpl - getSitePermissionForUser - error", e);
    }
    logger.info("SitePermissionServiceImpl - getSitePermissionForUser - ends");
    return sitePermission;
  }
}
