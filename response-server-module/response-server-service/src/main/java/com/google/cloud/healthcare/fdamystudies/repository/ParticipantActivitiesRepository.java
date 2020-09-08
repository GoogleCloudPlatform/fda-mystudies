/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.responsedatastore.model.ParticipantActivitiesBo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantActivitiesRepository
    extends JpaRepository<ParticipantActivitiesBo, Integer> {

  List<ParticipantActivitiesBo> findByStudyIdAndParticipantId(String studyId, String participantId);
}
