/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;

import com.google.cloud.healthcare.fdamystudies.beans.ConsentHistory;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import org.apache.commons.lang3.StringUtils;

public final class ConsentMapper {

  private ConsentMapper() {}

  public static ConsentHistory toConsentHistory(StudyConsentEntity studyConsent) {
    ConsentHistory consentHistory = new ConsentHistory();
    consentHistory.setId(studyConsent.getId());
    consentHistory.setConsentDocumentPath(studyConsent.getPdfPath());
    consentHistory.setConsentVersion(studyConsent.getVersion());

    String consentDate = DateTimeUtils.format(studyConsent.getParticipantStudy().getEnrolledDate());
    consentHistory.setConsentedDate(StringUtils.defaultIfEmpty(consentDate, NOT_APPLICABLE));

    consentHistory.setDataSharingPermissions(studyConsent.getParticipantStudy().getSharing());
    return consentHistory;
  }
}
