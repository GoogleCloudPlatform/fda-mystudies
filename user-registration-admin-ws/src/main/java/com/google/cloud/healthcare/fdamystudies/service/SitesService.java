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

import com.google.cloud.healthcare.fdamystudies.bean.ConsentDocumentBean;
import com.google.cloud.healthcare.fdamystudies.bean.DashboardBean;
import com.google.cloud.healthcare.fdamystudies.bean.EnableDisableParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.InviteParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantDetailsBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantRegistryResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantResponseBean;

public interface SitesService {

  public void addNewParticipant(ParticipantBean participant, Integer siteId, Integer userId);

  public ParticipantRegistryResponseBean getParticipants(
      String userId, Integer siteId, String onboardingStatus);

  public DashboardBean getSites(Integer userId);

  public ParticipantDetailsBean getParticipantDetails(
      Integer participantRegistrySiteId, Integer userId);

  public void addNewParticipant(
      ParticipantResponseBean participantRespBean, Integer siteId, Integer userId);

  void inviteParticipants(
      InviteParticipantBean inviteparticipantBean, Integer siteId, Integer userId);

  public void updateOnboardingStatus(
      EnableDisableParticipantBean bean, Integer siteId, Integer userId);

  ConsentDocumentBean getConsentDocument(Integer consentId, Integer userId);
}
