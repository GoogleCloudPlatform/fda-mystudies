/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface StudyPermissionRepository extends JpaRepository<StudyPermissionEntity, String> {

  @Query(
      "SELECT sp from StudyPermissionEntity sp where sp.study.id=:studyId and sp.urAdminUser.id=:userId")
  public Optional<StudyPermissionEntity> findByStudyIdAndUserId(String studyId, String userId);

  @Query("SELECT sp from StudyPermissionEntity sp where sp.study.id=:studyId")
  public List<StudyPermissionEntity> findByStudyId(String studyId);
}
