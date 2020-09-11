/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.repository.AuthInfoBORepository;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthInfoBODaoImpl implements AuthInfoBODao {

  private static final Logger logger = LoggerFactory.getLogger(AuthInfoBODaoImpl.class);
  @Autowired AuthInfoBORepository authInfoRepository;

  @Override
  public AuthInfoBO save(AuthInfoBO authInfo) {
    return authInfoRepository.save(authInfo);
  }

  @Override
  public Map<String, JSONArray> getDeviceTokenOfAllUsers(List<AppInfoDetailsBO> appInfos) {
    logger.info("AuthInfoBODaoImpl.getDeviceTokenOfAllUsers()-Start");
    JSONArray androidJsonArray = null;
    JSONArray iosJsonArray = null;
    Map<String, JSONArray> deviceMap = new HashMap<>();
    try {
      List<Integer> appInfoIds =
          appInfos.stream().map(a -> a.getAppInfoId()).distinct().collect(Collectors.toList());

      if (appInfoIds != null && !appInfoIds.isEmpty()) {
        List<AuthInfoBO> authInfos = authInfoRepository.findDevicesTokens(appInfoIds);
        if (authInfos != null && !authInfos.isEmpty()) {
          androidJsonArray = new JSONArray();
          iosJsonArray = new JSONArray();
          for (AuthInfoBO authInfoBO : authInfos) {
            String devicetoken = authInfoBO.getDeviceToken();
            String devicetype = authInfoBO.getDeviceType();
            if (devicetoken != null && devicetype != null) {
              if (devicetype.equalsIgnoreCase(AppConstants.DEVICE_ANDROID)) {
                androidJsonArray.put(devicetoken.trim());
              } else if (devicetype.equalsIgnoreCase(AppConstants.DEVICE_IOS)) {
                iosJsonArray.put(devicetoken.trim());
              } else {
                logger.error("Invalid Device Type");
              }
            }
          }
          deviceMap.put(AppConstants.DEVICE_ANDROID, androidJsonArray);
          deviceMap.put(AppConstants.DEVICE_IOS, iosJsonArray);
        }
      }
    } catch (Exception e) {
      logger.error("AuthInfoBODaoImpl.getDeviceTokenOfAllUsers()-error", e);
    }
    logger.info("AuthInfoBODaoImpl.getDeviceTokenOfAllUsers()-end ");
    return deviceMap;
  }
}
