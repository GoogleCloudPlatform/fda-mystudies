/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.List;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermission;

public interface SiteDao {

  public SiteBo getSiteDetails(Integer siteId);

  public List<SiteBo> getSitesForLocation(Integer locationId, Integer status);

  public List<StudyPermission> getStudyPermissionsOfUserByStudyIds(
      List<Integer> usersStudyIds, Integer userId);

  public ParticipantRegistrySite getParticipantSiteRegistry(Integer participantRegistrySiteId);

  public void updateEnrollment(
      String enrollment, Integer participantRegistrySiteId, String currentStatus);

  public List<ParticipantStudiesBO> getparticipantsEnrollment(Integer participantRegistrySiteId);

  public List<StudyConsentBO> getStudyConsentsOfParticipantStudyIds(
      List<Integer> participantStudyIds);

  public List<SiteBo> getSiteDetailsList(Integer studyId) throws SystemException;

  public StudyConsentBO getstudyConsentBO(Integer consentId);

  public List<SiteBo> getSites(List<Integer> studyIdList) throws SystemException;
}
