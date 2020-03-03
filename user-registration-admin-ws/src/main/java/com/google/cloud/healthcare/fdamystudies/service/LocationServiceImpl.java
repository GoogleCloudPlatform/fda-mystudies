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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.google.cloud.healthcare.fdamystudies.dao.LocationsDAO;
import com.google.cloud.healthcare.fdamystudies.dao.SiteDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileServiceDao;
import com.google.cloud.healthcare.fdamystudies.model.LocationBo;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@Service("locationService")
public class LocationServiceImpl implements LocationService {

  @Autowired private LocationsDAO locationDAO;

  @Autowired private UserProfileServiceDao userProfileServiceDao;

  @Autowired private SiteDao siteDao;

  @Override
  public LocationResponseBean getLocations(String authUserId, Integer locationId) {
    LocationResponseBean locationRespBean = new LocationResponseBean();
    List<LocationBean> locationBeans = new LinkedList<>();

    try {
      UserDetailsResponseBean bean = userProfileServiceDao.getUserProfile(authUserId);
      if (bean.getError().getApp_error_code() == ErrorCode.EC_200.code()) {
        ProfileRespBean profileBean = bean.getProfileRespBean();

        if (profileBean.getManageLocations() >= 1) {
          List<LocationBo> locationBos = locationDAO.getLocations(locationId);
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
                locationDAO.getStudiesForLocations(locationIds);

            if (locationStudies != null) {
              for (LocationBean lBean : locationBeans) {
                List<String> studies = locationStudies.get(lBean.getId());
                if (studies != null) {
                  lBean.setStudies(studies);
                  lBean.setStudiesCount(studies.size());
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
                  "error",
                  ErrorCode.EC_882.errorMessage());
          locationRespBean.setErrorBean(errorBean);
        }
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_861.code(),
                ErrorCode.EC_861.errorMessage(),
                "error",
                ErrorCode.EC_861.errorMessage());
        locationRespBean.setErrorBean(errorBean);
      }
    } catch (Exception e) {
      throw e;
    }
    return locationRespBean;
  }

  @Override
  public void addNewLocation(String authUserId, LocationBean locationBean) {
    UserDetailsResponseBean bean = userProfileServiceDao.getUserProfile(authUserId);
    if (bean.getError().getApp_error_code() == ErrorCode.EC_200.code()) {
      ProfileRespBean profileBean = bean.getProfileRespBean();
      if (profileBean.getManageLocations() == 2) {
        LocationBo locationBo = new LocationBo();
        locationBo.setName(locationBean.getName());
        locationBo.setDescription(locationBean.getDescription());
        locationBo.setCustomId(locationBean.getCustomId());
        locationBo.setCreatedBy(profileBean.getUserId());
        locationBo.setStatus("1");
        try {
          locationDAO.addNewLocation(locationBo);
        } catch (DataIntegrityViolationException e) {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_883.code(),
                  ErrorCode.EC_883.errorMessage(),
                  "error",
                  ErrorCode.EC_883.errorMessage());
          locationBean.setErrorBean(errorBean);
          return;
        }
        locationBean.setId(locationBo.getId());
        locationBean.setStatus(locationBo.getStatus());
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_882.code(),
                ErrorCode.EC_882.errorMessage(),
                "error",
                ErrorCode.EC_882.errorMessage());
        locationBean.setErrorBean(errorBean);
      }
    } else {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_861.code(),
              ErrorCode.EC_861.errorMessage(),
              "error",
              ErrorCode.EC_861.errorMessage());
      locationBean.setErrorBean(errorBean);
    }
  }

  @Override
  public void updateLocation(String authUserId, LocationBean locationBean) {
    UserDetailsResponseBean bean = userProfileServiceDao.getUserProfile(authUserId);
    SuccessBean successBean = new SuccessBean();
    if (bean.getError().getApp_error_code() == ErrorCode.EC_200.code()) {
      ProfileRespBean profileBean = bean.getProfileRespBean();
      if (profileBean.getManageLocations() == 2) {

        List<LocationBo> locationBos = locationDAO.getLocations(locationBean.getId());
        if (CollectionUtils.isEmpty(locationBos)) {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_881.code(),
                  ErrorCode.EC_881.errorMessage(),
                  "error",
                  ErrorCode.EC_881.errorMessage());
          locationBean.setErrorBean(errorBean);
        } else {

          LocationBo locationBo = locationBos.get(0);
          if ("0".equals(locationBean.getStatus())) {

            if ("0".equals(locationBo.getStatus())) {
              ErrorBean errorBean =
                  AppUtil.dynamicResponse(
                      ErrorCode.EC_886.code(),
                      ErrorCode.EC_886.errorMessage(),
                      "error",
                      ErrorCode.EC_886.errorMessage());
              locationBean.setErrorBean(errorBean);
              return;
            } else {
              // check active sites
              List<SiteBo> sites = siteDao.getSitesForLocation(locationBo.getId(), 1);
              if (!CollectionUtils.isEmpty(sites)) {
                ErrorBean errorBean =
                    AppUtil.dynamicResponse(
                        ErrorCode.EC_885.code(),
                        ErrorCode.EC_885.errorMessage(),
                        "error",
                        ErrorCode.EC_885.errorMessage());
                locationBean.setErrorBean(errorBean);
                return;
              } else {
                locationBo.setStatus(locationBean.getStatus());
                successBean.setMessage(SuccessBean.DECOMMISSION_SUCCESS);
                locationDAO.updateLocation(locationBo);
              }
            }
          } else if ("1".equals(locationBean.getStatus())) {
            // reactivate flow
            if ("1".equals(locationBo.getStatus())) {
              ErrorBean errorBean =
                  AppUtil.dynamicResponse(
                      ErrorCode.EC_887.code(),
                      ErrorCode.EC_887.errorMessage(),
                      "error",
                      ErrorCode.EC_887.errorMessage());
              locationBean.setErrorBean(errorBean);
              return;
            } else {
              locationBo.setStatus(locationBean.getStatus());
              successBean.setMessage(SuccessBean.REACTIVE_SUCCESS);
              locationDAO.updateLocation(locationBo);
            }
          } else {
            locationBo.setName(
                locationBean.getName() == null ? locationBo.getName() : locationBean.getName());
            locationBo.setDescription(
                locationBean.getDescription() == null
                    ? locationBo.getDescription()
                    : locationBean.getDescription());
            locationBo.setStatus(locationBo.getStatus());
            locationDAO.updateLocation(locationBo);
          }

          locationBean.setCustomId(locationBo.getCustomId());
          locationBean.setDescription(locationBo.getDescription());
          locationBean.setName(locationBo.getName());
          locationBean.setStatus(locationBo.getStatus());
          locationBean.setSuccessBean(successBean);
        }
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_882.code(),
                ErrorCode.EC_882.errorMessage(),
                "error",
                ErrorCode.EC_882.errorMessage());
        locationBean.setErrorBean(errorBean);
      }
    } else {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_861.code(),
              ErrorCode.EC_861.errorMessage(),
              "error",
              ErrorCode.EC_861.errorMessage());
      locationBean.setErrorBean(errorBean);
    }
  }
}
