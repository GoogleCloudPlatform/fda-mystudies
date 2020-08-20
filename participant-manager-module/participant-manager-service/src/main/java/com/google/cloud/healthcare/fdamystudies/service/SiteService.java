/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.ImportParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.InviteParticipantRequest;
import com.google.cloud.healthcare.fdamystudies.beans.InviteParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateTargetEnrollmentRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateTargetEnrollmentResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SiteService {

  public SiteResponse addSite(SiteRequest siteRequest);

  public ParticipantRegistryResponse getParticipants(
      String userId, String siteId, String onboardingStatus);

  public ParticipantResponse addNewParticipant(ParticipantDetailRequest participant, String userId);

  public SiteStatusResponse toggleSiteStatus(String userId, String siteId);

  public ParticipantDetailResponse getParticipantDetails(
      String participantRegistrySiteId, String userId);

  public InviteParticipantResponse inviteParticipants(
      InviteParticipantRequest inviteparticipantBean);

  public ImportParticipantResponse importParticipants(
      String userId, String siteId, MultipartFile multipartFile);

  public ParticipantStatusResponse updateOnboardingStatus(ParticipantStatusRequest request);

  public SiteDetailsResponse getSites(String userId);

  public UpdateTargetEnrollmentResponse updateTargetEnrollment(
      UpdateTargetEnrollmentRequest enrollmentRequest);
}
