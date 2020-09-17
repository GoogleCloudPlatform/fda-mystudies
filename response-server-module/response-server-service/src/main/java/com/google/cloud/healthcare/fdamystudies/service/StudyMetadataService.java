/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyActivityMetadataRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;

public interface StudyMetadataService {
  public void saveStudyMetadata(StudyMetadataBean studyMetadataBean)
      throws ProcessResponseException;

  QuestionnaireActivityStructureBean getStudyActivityMetadata(
      String applicationId, StudyActivityMetadataRequestBean studyActivityMetadataRequestBean)
      throws ProcessResponseException;
}
