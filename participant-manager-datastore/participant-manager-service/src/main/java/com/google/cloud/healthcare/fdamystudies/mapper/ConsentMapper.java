/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CONSENT_DATE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.DATA_SHARING;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PDF_PATH;
import com.google.api.services.healthcare.v1.model.ConsentArtifact;
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

    String consentDate = DateTimeUtils.format(studyConsent.getConsentDate());
    consentHistory.setConsentedDate(StringUtils.defaultIfEmpty(consentDate, NOT_APPLICABLE));

    consentHistory.setDataSharingPermissions(studyConsent.getSharing());
    return consentHistory;
  }

  public static ConsentHistory toConsentHistory(
      ConsentArtifact consentArtifact, String consentStoreId) {
    ConsentHistory consentHistory = new ConsentHistory();
    String name = consentArtifact.getName();
    consentHistory.setId(
        consentStoreId + "@" + name.substring(name.lastIndexOf("/") + 1, name.length()));
    String pdfPath = consentArtifact.getMetadata().get(PDF_PATH);
    consentHistory.setConsentDocumentPath(pdfPath);

    consentHistory.setCreateTimeStamp(
        pdfPath.substring(pdfPath.lastIndexOf("_") + 1, pdfPath.indexOf(".pdf")));

    consentHistory.setConsentVersion(consentArtifact.getConsentContentVersion());

    String consentDate = consentArtifact.getMetadata().get(CONSENT_DATE);
    consentHistory.setConsentedDate(StringUtils.defaultIfEmpty(consentDate, NOT_APPLICABLE));

    consentHistory.setDataSharingPermissions(consentArtifact.getMetadata().get(DATA_SHARING));
    return consentHistory;
  }
}
