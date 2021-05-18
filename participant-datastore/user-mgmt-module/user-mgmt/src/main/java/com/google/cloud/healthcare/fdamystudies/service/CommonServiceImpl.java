/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private CommonDao commonDao;

  private XLogger logger = XLoggerFactory.getXLogger(CommonServiceImpl.class.getName());

  @Override
  @Transactional(readOnly = true)
  public String validatedUserAppDetailsByAllApi(String userId, String email, String appId) {

    logger.entry("Begin validatedUserAppDetailsByAllApi()");
    String message = "";
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();

    appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId);
    message =
        commonDao.validatedUserAppDetailsByAllApi(userId, email, appOrgInfoBean.getAppInfoId());

    logger.exit("validatedUserAppDetailsByAllApi() - ends");
    return message;
  }

  @Override
  @Transactional(readOnly = true)
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String emailId, String appId) {

    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    logger.entry("Begin getUserAppDetailsByAllApi()");

    appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId);

    logger.exit("getUserAppDetailsByAllApi() - ends");
    return appOrgInfoBean;
  }
}
