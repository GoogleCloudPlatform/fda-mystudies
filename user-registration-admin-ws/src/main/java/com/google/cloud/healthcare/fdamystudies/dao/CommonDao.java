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
import com.google.cloud.healthcare.fdamystudies.model.MailMessages;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;

public interface CommonDao {
  public List<SitePermission> getSitePermissions(Integer userId);

  public List<ParticipantRegistrySite> getParticipantRegistryOfSites(List<Integer> siteIds);

  public List<ParticipantRegistrySite> getParticipantRegistry(Integer studyId, String email);

  public List<ParticipantStudiesBO> getParticipantsEnrollmentsOfSites(List<Integer> usersSiteIds);

  public String addToMailMessages(List<MailMessages> mailMessages);

  public void processEmail();
}
