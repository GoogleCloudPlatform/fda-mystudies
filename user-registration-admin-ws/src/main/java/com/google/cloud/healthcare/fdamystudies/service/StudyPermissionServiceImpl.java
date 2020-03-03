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
import com.google.cloud.healthcare.fdamystudies.dao.StudyPermissionDao;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermission;

@Service
public class StudyPermissionServiceImpl implements StudyPermissionService {

  private static Logger logger = LoggerFactory.getLogger(StudyPermissionServiceImpl.class);

  @Autowired private StudyPermissionDao studyPermissionDao;

  @Override
  public StudyPermission getStudyPermissionForUser(Integer studyId, Integer userId)
      throws SystemException {
    logger.info("StudyPermissionServiceImpl - getStudyPermissionForUser() : starts");
    if (userId != null && studyId != null) {
      logger.info("StudyPermissionServiceImpl - getStudyPermissionForUser() : ends");
      return studyPermissionDao.getStudyPermissionForUser(studyId, userId);
    } else {
      logger.info("StudyPermissionServiceImpl - getStudyPermissionForUser() : ends with null");
      return null;
    }
  }
}
