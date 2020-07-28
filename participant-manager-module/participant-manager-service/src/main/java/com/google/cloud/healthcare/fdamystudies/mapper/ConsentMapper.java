/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.cloud.healthcare.fdamystudies.beans.ConsentHistory;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;

public final class ConsentMapper {

  private ConsentMapper() {}

  public static List<ConsentHistory> toStudyConsents(List<StudyConsentEntity> studyConsents) {
    List<ConsentHistory> consentHistories = new ArrayList<>();
    for (StudyConsentEntity studyCosent : CollectionUtils.emptyIfNull(studyConsents)) {
      ConsentHistory consentHistory = new ConsentHistory();
      consentHistory.setId(studyCosent.getId());
      consentHistory.setConsentDocumentPath(studyCosent.getPdfPath());
      consentHistory.setConsentVersion(studyCosent.getVersion());

      String consentDate =
          DateTimeUtils.format(studyCosent.getParticipantStudy().getEnrolledDate());
      consentHistory.setConsentedDate(StringUtils.defaultIfEmpty(consentDate, NOT_APPLICABLE));

      consentHistory.setDataSharingPermissions(studyCosent.getParticipantStudy().getSharing());
      consentHistories.add(consentHistory);
    }
    return consentHistories;
  }
}
