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
import org.springframework.web.client.RestTemplate;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired private CommonDao commonDao;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public String validatedUserAppDetailsByAllApi(String userId, String email, String appId) {
    logger.info("UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - starts");
    String message = "";
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();

    appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId);
    message =
        commonDao.validatedUserAppDetailsByAllApi(userId, email, appOrgInfoBean.getAppInfoId());

    logger.info("UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - ends");
    return message;
  }

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String emailId, String appId) {
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    logger.info("MyStudiesUserRegUtil getUserAppDetailsByAllApi() - starts");

    appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId);

    logger.info("MyStudiesUserRegUtil getUserAppDetailsByAllApi() - ends");
    return appOrgInfoBean;
  }
}
