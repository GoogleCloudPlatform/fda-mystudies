/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.LocationBean;
import com.google.cloud.healthcare.fdamystudies.bean.ProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserDetailsResponseBean;
import com.google.cloud.healthcare.fdamystudies.dao.LocationsDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileServiceDao;
import com.google.cloud.healthcare.fdamystudies.model.LocationBo;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@Service("locationService")
public class LocationServiceImpl implements LocationService {

  private static final String ERROR = "error";

  @Autowired private LocationsDao locationDao;

  @Autowired private UserProfileServiceDao userProfileServiceDao;

  private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);

  @Override
  public void addNewLocation(Integer userId, LocationBean locationBean) {
    logger.info("LocationServiceImpl - addNewLocation() - starts");
    UserDetailsResponseBean bean = userProfileServiceDao.getUserProfileById(userId);
    if (bean.getError().getAppErrorCode() == ErrorCode.EC_200.code()) {
      ProfileRespBean profileBean = bean.getProfileRespBean();
      if (profileBean != null && 2 == profileBean.getManageLocations()) {
        LocationBo locationBo = new LocationBo();
        locationBo.setName(locationBean.getName());
        locationBo.setDescription(locationBean.getDescription());
        locationBo.setCustomId(locationBean.getCustomId());
        locationBo.setCreatedBy(profileBean.getUserId());
        locationBo.setStatus("1");
        locationBo.setIsdefault("N");
        try {
          locationDao.addNewLocation(locationBo);
          locationBean.setSuccessBean(new SuccessBean(SuccessBean.ADD_LOCATION_SUCCESS));
        } catch (DataIntegrityViolationException e) {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_883.code(),
                  ErrorCode.EC_883.errorMessage(),
                  ERROR,
                  ErrorCode.EC_883.errorMessage());
          locationBean.setErrorBean(errorBean);
          logger.info("LocationServiceImpl - addNewLocation() - ends");
          return;
        }
        locationBean.setId(locationBo.getId());
        locationBean.setStatus(locationBo.getStatus());
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_882.code(),
                ErrorCode.EC_882.errorMessage(),
                ERROR,
                ErrorCode.EC_882.errorMessage());
        locationBean.setErrorBean(errorBean);
      }
    } else {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_861.code(),
              ErrorCode.EC_861.errorMessage(),
              ERROR,
              ErrorCode.EC_861.errorMessage());
      locationBean.setErrorBean(errorBean);
    }
    logger.info("LocationServiceImpl - addNewLocation() - ends");
  }
}
