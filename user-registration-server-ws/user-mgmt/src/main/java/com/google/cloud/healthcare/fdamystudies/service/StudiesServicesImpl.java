/*
*Copyright 2020 Google LLC
* 
*Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
*or at https://opensource.org/licenses/MIT.
*/
package com.google.cloud.healthcare.fdamystudies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.dao.StudiesDao;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;

@Service
public class StudiesServicesImpl implements StudiesServices {

  private static Logger logger = LoggerFactory.getLogger(StudiesServicesImpl.class);

  @Autowired private StudiesDao studiesDao;

  @Override
  public ErrorBean saveStudyMetadata(StudyMetadataBean studyMetadataBean) {
    logger.info("StudiesServicesImpl - saveStudyMetadata() : starts");
    ErrorBean errorBean = null;
    try {
      errorBean = studiesDao.saveStudyMetadata(studyMetadataBean);
    } catch (Exception e) {
      logger.error("StudiesServicesImpl - saveStudyMetadata() : error ", e);
      return new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
    }
    logger.info("StudiesServicesImpl - saveStudyMetadata() : ends");
    return errorBean;
  }
}
