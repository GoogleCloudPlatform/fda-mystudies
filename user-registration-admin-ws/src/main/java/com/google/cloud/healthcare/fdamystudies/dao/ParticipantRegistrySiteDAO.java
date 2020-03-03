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
import java.util.Map;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;

public interface ParticipantRegistrySiteDAO {

  public void saveParticipantRegistry(ParticipantRegistrySite participantRegistrySite);

  public ParticipantRegistrySite getParticipantRegistry(Integer participantSiteId)
      throws SystemException;

  public List<ParticipantRegistrySite> getParticipantRegistryForSite(
      String onboardingStatus, Integer siteId);

  public Map<String, Integer> getParticipantCountByOnboardingStatus(Integer siteId);

  public String updateOnboardingStatus(List<Integer> ids, String status);

  public List<ParticipantRegistrySite> getParticipantRegistry(List<Integer> participantSiteId);
}
