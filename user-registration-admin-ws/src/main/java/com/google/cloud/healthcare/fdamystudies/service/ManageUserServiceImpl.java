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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.bean.AuthRegistrationResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.DeleteAccountInfoResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.RegisterUser;
import com.google.cloud.healthcare.fdamystudies.bean.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.bean.SetUpAccountResponse;
import com.google.cloud.healthcare.fdamystudies.bean.User;
import com.google.cloud.healthcare.fdamystudies.dao.AppPermissionDao;
import com.google.cloud.healthcare.fdamystudies.dao.AppsDao;
import com.google.cloud.healthcare.fdamystudies.dao.SiteDao;
import com.google.cloud.healthcare.fdamystudies.dao.SitePermissionDAO;
import com.google.cloud.healthcare.fdamystudies.dao.StudyInfoDao;
import com.google.cloud.healthcare.fdamystudies.dao.StudyPermissionDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserRegAdminUserDao;
import com.google.cloud.healthcare.fdamystudies.exception.DuplicateEntryFoundException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidEmailIdException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UserNotInvited;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminUser;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSUtil;

@Service
public class ManageUserServiceImpl implements ManageUserService {

  public static final Logger logger = LoggerFactory.getLogger(ManageUserServiceImpl.class);

  @Autowired private AppsDao appsDao;

  @Autowired private StudyInfoDao studyInfoDao;

  @Autowired private SiteDao siteDao;

  @Autowired private UserRegAdminUserDao adminDao;

  @Autowired private AppPermissionDao appPermission;

  @Autowired private StudyPermissionDao studyPermissionDao;

  @Autowired private SitePermissionDAO sitePermissionDAO;

  @Autowired private UserRegAdminUserDao userRegAdminUserDao;

  @Autowired private URWebAppWSUtil urWebAppWSUtil;

  @Override
  public String saveUser(String userId, RegisterUser user)
      throws SystemException, InvalidUserIdException, DuplicateEntryFoundException {

    logger.info("(Service)...ManageUserServiceImpl.saveUser()...Started");
    String message = AppConstants.FAILURE;
    if (user != null && userId != null) {

      try {
        UserRegAdminUser adminDetails = adminDao.checkPermission(Integer.valueOf(userId));
        if (adminDetails != null && Boolean.TRUE.equals(adminDetails.getSuperAdmin())) {

          if (Boolean.FALSE.equals(adminDao.checkDuplicateEntryUsingEmailId(user.getEmail()))) {
            logger.info("SuperAdmin: " + user.getSuperAdmin());
            if (Boolean.TRUE.equals(user.getSuperAdmin())) {
              // Give permission to all the apps and studies and sites by making the superAdmin
              // column as 1
              adminDao.saveDetails(user, userId);
            }

          } else throw new DuplicateEntryFoundException();

          message = AppConstants.SUCCESS;
        } else throw new InvalidUserIdException();

        return message;
      } catch (InvalidUserIdException e) {
        logger.error("(Service)...ManageUserServiceImpl.saveUser()...Ended: (ERROR)", e);
        throw e;
      } catch (DuplicateEntryFoundException e) {
        logger.error("(Service)...ManageUserServiceImpl.saveUser()...Ended: (ERROR)", e);
        throw e;
      } /*catch (Exception e) {
          logger.error("(Service)...ManageUserServiceImpl.saveUser()...Ended: (ERROR)", e);
          throw new SystemException();
        }*/
    } else {
      return message;
    }
  }

  @Override
  public List<User> getUsers(String userId) throws SystemException, InvalidUserIdException {
    logger.info("(Service)...ManageUserServiceImpl.getUsers()...Started");
    List<User> userList = new ArrayList<>();

    if (userId != null) {
      try {
        UserRegAdminUser userRegAdminUser =
            userRegAdminUserDao.checkPermission(Integer.valueOf(userId));
        logger.info("(Service)...userRegAdminUser: " + userRegAdminUser);

        if (userRegAdminUser != null && Boolean.TRUE.equals(userRegAdminUser.getSuperAdmin())) {

          List<AppInfoDetailsBO> appDetails = appsDao.getAllApps();
          logger.info("(Service)...appDetails size: " + appDetails.size());
          List<Integer> appInfoIdList = new ArrayList<>();
          if (appDetails != null && !appDetails.isEmpty()) {
            appDetails
                .stream()
                .map((app) -> appInfoIdList.add(app.getAppInfoId()))
                .collect(Collectors.toList());
          }
          logger.info("(Service)...appInfoIdList: " + appInfoIdList);
          logger.info("(Service)...***********************************************");

          List<StudyInfoBO> studyList = studyInfoDao.getStudies(appInfoIdList);
          logger.info("(Service)...studyList size: " + studyList.size());
          List<Integer> studyIdList = new ArrayList<>();
          if (studyList != null && !studyList.isEmpty()) {
            studyList
                .stream()
                .map((study) -> studyIdList.add(study.getId()))
                .collect(Collectors.toList());
          }
          logger.info("(Service)...studyIdList: " + studyIdList);
          logger.info("(Service)...***********************************************");

          List<SiteBo> siteList = siteDao.getSites(studyIdList);
          logger.info("(Service)...siteList size: " + siteList.size());
          logger.info("(Service)...***********************************************");

          return userList;
        } else throw new InvalidUserIdException();

      } catch (InvalidUserIdException e) {
        logger.info("(Service)...ManageUserServiceImpl.getUsers()...Ended: (ERROR)", e);
        throw e;
      } catch (Exception e) {
        logger.info("(Service)...ManageUserServiceImpl.getUsers()...Ended: (ERROR)", e);
        throw new SystemException();
      }
    } else {
      logger.info("(Service)...ManageUserServiceImpl.getUsers()...Ended");
      return null;
    }
  }

  @Override
  public SetUpAccountResponse saveUser(SetUpAccountRequest request)
      throws SystemException, UserNotInvited, InvalidEmailIdException {

    logger.info("ManageUserServiceImpl - saveUser() : starts");
    SetUpAccountResponse serviceResponse = null;
    AuthRegistrationResponseBean authResponse = null;
    if (request != null) {

      try {
        Boolean result = userRegAdminUserDao.checkEmailIdExists(request.getEmail());

        if (Boolean.TRUE.equals(result)) {
          authResponse = urWebAppWSUtil.registerUserInAuthServer(request);

          if (authResponse != null && "OK".equals(authResponse.getMessage())) {
            String message = userRegAdminUserDao.updateUser(request, authResponse.getUserId());
            if ("SUCCESS".equals(message)) {
              serviceResponse = BeanUtil.getBean(SetUpAccountResponse.class);
              serviceResponse.setStatusCode("200");
              serviceResponse.setMessage("SUCCESS");
              logger.info("ManageUserServiceImpl - saveUser() : ends successfully");
              return serviceResponse;
            } else {
              throw new InvalidEmailIdException();
            }
          } else {
            if (authResponse != null) {
              serviceResponse = BeanUtil.getBean(SetUpAccountResponse.class);
              serviceResponse.setStatusCode(authResponse.getCode());
              serviceResponse.setMessage(authResponse.getMessage());
              logger.info("ManageUserServiceImpl - saveUser() : ends");
              return serviceResponse;
            } else {
              throw new SystemException();
            }
          }
        } else {
          throw new UserNotInvited();
        }
      } catch (UserNotInvited e) {
        logger.error("ManageUserServiceImpl - saveUser() : error ", e);
        throw e;
      } catch (SystemException e) {
        logger.error("ManageUserServiceImpl - saveUser() : error ", e);
        if (authResponse != null) {
          DeleteAccountInfoResponseBean deleteUserResponse =
              urWebAppWSUtil.deleteUserInfoInAuthServer(
                  authResponse.getUserId(),
                  authResponse.getClientToken(),
                  authResponse.getAccessToken());
          logger.error(
              "ManageUserServiceImpl - saveUser(): deleteUserResponse = " + deleteUserResponse);
        }
        logger.error(request.getEmail() + " record has deleted from auth server for some reason");
        throw e;
      } catch (InvalidEmailIdException e) {
        logger.error("ManageUserServiceImpl - saveUser() : error ", e);
        throw e;
      } catch (Exception e) {
        if (authResponse != null) {
          DeleteAccountInfoResponseBean deleteUserResponse =
              urWebAppWSUtil.deleteUserInfoInAuthServer(
                  authResponse.getUserId(),
                  authResponse.getClientToken(),
                  authResponse.getAccessToken());
          logger.info(
              "ManageUserServiceImpl - saveUser(): deleteUserResponse = " + deleteUserResponse);
        }
        logger.error(request.getEmail() + " record has deleted from auth server for some reason");
        logger.error("ManageUserServiceImpl - saveUser() : error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("ManageUserServiceImpl - saveUser() : ends");
      return serviceResponse;
    }
  }
}
