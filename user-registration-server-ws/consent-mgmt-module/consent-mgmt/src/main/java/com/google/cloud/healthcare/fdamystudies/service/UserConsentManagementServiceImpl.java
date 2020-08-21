/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.consent.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.consent.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.dao.UserConsentManagementDao;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConsentManagementServiceImpl implements UserConsentManagementService {
  @Autowired UserConsentManagementDao userConsentManagementDao;

  @Autowired FileStorageService cloudStorageService;

  private static final Logger logger =
      LoggerFactory.getLogger(UserConsentManagementServiceImpl.class);

  @Override
  public ConsentStudyResponseBean getStudyConsentDetails(
      String userId, Integer studyId, String consentVersion) {

    logger.info("UserConsentManagementServiceImpl getStudyConsentDetails() - Started ");
    StudyConsentBO studyConsent = null;
    ParticipantStudiesBO participantStudiesBO = null;
    ConsentStudyResponseBean consentStudyResponseBean = new ConsentStudyResponseBean();

    studyConsent = userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion);

    if (studyConsent != null) {
      if (studyConsent.getVersion() != null) {
        consentStudyResponseBean.getConsent().setVersion(studyConsent.getVersion());
      }
      if (studyConsent.getPdf() != null) {
        consentStudyResponseBean.getConsent().setContent(studyConsent.getPdf());
      }

      if (studyConsent.getPdfStorage() == 1) {
        String path = studyConsent.getPdfPath();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cloudStorageService.downloadFileTo(path, baos);
        consentStudyResponseBean
            .getConsent()
            .setContent(new String(Base64.getEncoder().encode(baos.toByteArray())));
      }
      consentStudyResponseBean.getConsent().setType("application/pdf");
      participantStudiesBO = userConsentManagementDao.getParticipantStudies(studyId, userId);
      if (participantStudiesBO != null) {
        consentStudyResponseBean.setSharing(participantStudiesBO.getSharing());
      }
    }

    logger.info("UserConsentManagementServiceImpl getStudyConsentDetails() - Ends ");
    return consentStudyResponseBean;
  }
}
