/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.beans.UserResourceBean;
import com.google.cloud.healthcare.fdamystudies.model.UserInstitutionEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserInstitutionRepository;
import com.google.cloud.healthcare.fdamystudies.service.CloudStorageService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetUserInstitutionResources {
  @Autowired UserInstitutionRepository userInstitutionRepository;

  @Autowired CloudStorageService cloudStorageService;

  private static final UserResourceBean.ResourceType resourceType =
      UserResourceBean.ResourceType.INSTITUTION_RESOURCE;

  // Returns UserResourceBeans for the institution that `userId` belongs to.
  // Can be an empty list.
  public List<UserResourceBean> getInstitutionResourcesForUser(String userId) {
    Optional<UserInstitutionEntity> maybeUserInstitution =
        userInstitutionRepository.findByUserUserId(userId);
    if (!maybeUserInstitution.isPresent()) return new ArrayList<>();
    UserInstitutionEntity userInstitution = maybeUserInstitution.get();

    List<CloudStorageService.InstitutionResource> streams =
        cloudStorageService.getAllInstitutionResources(userInstitution.getInstitutionId());
    if (streams.isEmpty()) return new ArrayList<>();

    List<UserResourceBean> resources = new ArrayList<>();
    for (CloudStorageService.InstitutionResource institutionResource : streams) {
      String content = new String(institutionResource.stream.toByteArray());
      resources.add(
          new UserResourceBean(
              institutionResource.title, content, resourceType, institutionResource.hash));
    }
    return resources;
  }
}
