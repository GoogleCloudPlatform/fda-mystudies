/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.LocationBean;
import com.google.cloud.healthcare.fdamystudies.bean.LocationResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserDetailsResponseBean;
import com.google.cloud.healthcare.fdamystudies.dao.LocationsDao;
import com.google.cloud.healthcare.fdamystudies.dao.SiteDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileServiceDao;
import com.google.cloud.healthcare.fdamystudies.model.LocationBo;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@Service("locationService")
public class LocationServiceImpl implements LocationService {

  private static final String ERROR = "error";

  @Autowired private LocationsDao locationDao;

  @Autowired private UserProfileServiceDao userProfileServiceDao;

  @Autowired private SiteDao siteDao;

  private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);

  @Override
  public LocationResponseBean getLocations(Integer userId, Integer locationId) {
    logger.info("LocationServiceImpl - getLocations() - starts");
    LocationResponseBean locationRespBean = new LocationResponseBean();
    List<LocationBean> locationBeans = new LinkedList<>();

    try {
      UserDetailsResponseBean bean = userProfileServiceDao.getUserProfileById(userId);
      if (bean.getError().getAppErrorCode() == ErrorCode.EC_200.code()) {
        ProfileRespBean profileBean = bean.getProfileRespBean();

        if (profileBean.getManageLocations() >= 1) {
          List<LocationBo> locationBos = locationDao.getLocations(locationId);
          if (!CollectionUtils.isEmpty(locationBos)) {
            List<Integer> locationIds = new LinkedList<>();
            for (LocationBo locationBo : locationBos) {
              LocationBean locationBean = new LocationBean();
              locationBean.setId(locationBo.getId());
              locationBean.setName(locationBo.getName());
              locationBean.setDescription(locationBo.getDescription());
              locationBean.setCustomId(locationBo.getCustomId());
              locationBean.setStatus(locationBo.getStatus());
              locationBeans.add(locationBean);
              locationIds.add(locationBo.getId());
            }

            Map<Integer, List<String>> locationStudies =
                locationDao.getStudiesForLocations(locationIds);

            if (locationStudies != null) {
              for (LocationBean locBean : locationBeans) {
                List<String> studies = locationStudies.get(locBean.getId());
                if (studies != null) {
                  locBean.setStudies(studies);
                  locBean.setStudiesCount(studies.size());
                }
              }
            }

            locationRespBean.setLocations(locationBeans);
          }
        } else {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_882.code(),
                  ErrorCode.EC_882.errorMessage(),
                  ERROR,
                  ErrorCode.EC_882.errorMessage());
          locationRespBean.setErrorBean(errorBean);
        }
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_861.code(),
                ErrorCode.EC_861.errorMessage(),
                ERROR,
                ErrorCode.EC_861.errorMessage());
        locationRespBean.setErrorBean(errorBean);
      }
    } catch (Exception e) {
      logger.info("LocationServiceImpl - getLocations() - error");
      throw e;
    }
    logger.info("LocationServiceImpl - getLocations() - ends");
    return locationRespBean;
  }

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

  @Override
  public void updateLocation(Integer userId, LocationBean locationBean) {
    logger.info("LocationServiceImpl - updateLocation() - starts");
    UserDetailsResponseBean bean = userProfileServiceDao.getUserProfileById(userId);
    SuccessBean successBean = new SuccessBean();
    if (bean.getError().getAppErrorCode() == ErrorCode.EC_200.code()) {
      ProfileRespBean profileBean = bean.getProfileRespBean();
      if (profileBean.getManageLocations() == 2) {

        List<LocationBo> locationBos = locationDao.getLocations(locationBean.getId());
        if (CollectionUtils.isEmpty(locationBos)) {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_881.code(),
                  ErrorCode.EC_881.errorMessage(),
                  ERROR,
                  ErrorCode.EC_881.errorMessage());
          locationBean.setErrorBean(errorBean);
          return;
        }
        LocationBo locationBo = locationBos.get(0);
        if ("Y".equals(locationBo.getIsdefault())) {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_888.code(),
                  ErrorCode.EC_888.errorMessage(),
                  AppConstants.ERROR,
                  ErrorCode.EC_888.errorMessage());
          locationBean.setErrorBean(errorBean);
          logger.info("LocationServiceImpl - updateLocation() - ends");
          return;
        }
        if (AppConstants.STATUS_DEACTIVE.equals(locationBean.getStatus())) {

          if (AppConstants.STATUS_DEACTIVE.equals(locationBo.getStatus())) {
            ErrorBean errorBean =
                AppUtil.dynamicResponse(
                    ErrorCode.EC_886.code(),
                    ErrorCode.EC_886.errorMessage(),
                    ERROR,
                    ErrorCode.EC_886.errorMessage());
            locationBean.setErrorBean(errorBean);
            logger.info("LocationServiceImpl - updateLocation() - ends");
            return;
          }
          // check active sites
          List<SiteBo> sites =
              siteDao.getSitesForLocation(
                  locationBo.getId(), Integer.parseInt(AppConstants.STATUS_ACTIVE));
          if (!CollectionUtils.isEmpty(sites)) {
            ErrorBean errorBean =
                AppUtil.dynamicResponse(
                    ErrorCode.EC_885.code(),
                    ErrorCode.EC_885.errorMessage(),
                    ERROR,
                    ErrorCode.EC_885.errorMessage());
            locationBean.setErrorBean(errorBean);
            logger.info("LocationServiceImpl - updateLocation() - ends");
            return;
          }
          locationBo.setStatus(locationBean.getStatus());
          successBean.setMessage(SuccessBean.DECOMMISSION_SUCCESS);
          locationDao.updateLocation(locationBo);

        } else if (AppConstants.STATUS_ACTIVE.equals(locationBean.getStatus())) {
          // reactivate flow
          if (AppConstants.STATUS_ACTIVE.equals(locationBo.getStatus())) {
            ErrorBean errorBean =
                AppUtil.dynamicResponse(
                    ErrorCode.EC_887.code(),
                    ErrorCode.EC_887.errorMessage(),
                    ERROR,
                    ErrorCode.EC_887.errorMessage());
            locationBean.setErrorBean(errorBean);
            logger.info("LocationServiceImpl - updateLocation() - ends");
            return;
          }
          locationBo.setStatus(locationBean.getStatus());
          successBean.setMessage(SuccessBean.REACTIVE_SUCCESS);
          locationDao.updateLocation(locationBo);

        } else {
          locationBo.setName(
              locationBean.getName() == null ? locationBo.getName() : locationBean.getName());
          locationBo.setDescription(
              locationBean.getDescription() == null
                  ? locationBo.getDescription()
                  : locationBean.getDescription());
          locationBo.setStatus(locationBo.getStatus());
          locationDao.updateLocation(locationBo);
        }

        locationBean.setCustomId(locationBo.getCustomId());
        locationBean.setDescription(locationBo.getDescription());
        locationBean.setName(locationBo.getName());
        locationBean.setStatus(locationBo.getStatus());
        locationBean.setSuccessBean(successBean);

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
    logger.info("LocationServiceImpl - updateLocation() - ends");
  }
}
