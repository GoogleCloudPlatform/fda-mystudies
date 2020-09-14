/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired private CommonDao commonDao;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  @Transactional(readOnly = true)
  public String validatedUserAppDetailsByAllApi(
      String userId, String email, String appId, String orgId) {
    logger.info("UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - starts");
    String message = "";
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    try {
      appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId, orgId);
      message =
          commonDao.validatedUserAppDetailsByAllApi(
              userId, email, appOrgInfoBean.getAppInfoId(), appOrgInfoBean.getOrgInfoId());
    } catch (Exception e) {
      logger.error(
          "UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - ends");
    return message;
  }

  @Override
  @Transactional(readOnly = true)
  public AppOrgInfoBean getUserAppDetailsByAllApi(
      String userId, String emailId, String appId, String orgId) {
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    logger.info("MyStudiesUserRegUtil getUserAppDetailsByAllApi() - starts");
    try {
      appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId, orgId);
    } catch (Exception e) {
      logger.error("MyStudiesUserRegUtil getUserAppDetailsByAllApi() - error() ", e);
    }

    logger.info("MyStudiesUserRegUtil getUserAppDetailsByAllApi() - ends");
    return appOrgInfoBean;
  }
}
