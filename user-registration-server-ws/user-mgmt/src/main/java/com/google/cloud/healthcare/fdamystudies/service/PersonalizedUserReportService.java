/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.UserResourceBean;
import com.google.cloud.healthcare.fdamystudies.model.PersonalizedUserReportBO;
import com.google.cloud.healthcare.fdamystudies.repository.PersonalizedUserReportRepository;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonalizedUserReportService {

  @Autowired PersonalizedUserReportRepository repository;

  private static final UserResourceBean.ResourceType resourceType =
      UserResourceBean.ResourceType.PERSONALIZED_REPORT;

  public List<UserResourceBean> getLatestPersonalizedUserReports(String userId, String studyId) {
    return repository.findByUserDetailsUserIdAndStudyInfoCustomId(userId, studyId).stream()
        .collect(
            Collectors.toMap(
                PersonalizedUserReportBO::getReportTitle,
                Function.identity(),
                BinaryOperator.maxBy(
                    Comparator.comparing(PersonalizedUserReportBO::getCreationTime))))
        .entrySet()
        .stream()
        .map(
            e ->
                new UserResourceBean(
                    e.getKey(),
                    e.getValue().getReportContent(),
                    resourceType,
                    e.getValue().getId().toString()))
        .collect(Collectors.toList());
  }
}
