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

import java.util.List;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;

public interface CommonService {
  public Integer validateAccessToken(
      String userId, String accessToken, String clientId, String secretKey);

  public List<SitePermission> getSitePermissionsOfUser(Integer userId);

  public List<SiteBo> getAllSite();

  public ParticipantStudiesBO getParticipantStudiesBOs(Integer participantRegistryId);

  public List<ParticipantRegistrySite> getParticipantRegistry(Integer studyId, String email);
}
