/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.map.HashedMap;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantStudiesInfoDaoImpl implements ParticipantStudiesInfoDao {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(ParticipantStudiesInfoDaoImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Autowired private UserDetailsRepository userDetailsRepository;

  @Override
  @SuppressWarnings("unchecked")
  public List<ParticipantStudyEntity> getParticipantStudiesInfo(String userDetailsId) {

    List<ParticipantStudyEntity> participantStudiesList = null;
    logger.entry("Begin getParticipantStudiesInfo()");
    if (userDetailsId != null) {
      Session session = this.sessionFactory.getCurrentSession();

      Optional<UserDetailsEntity> optUserDetails =
          userDetailsRepository.findByUserId(userDetailsId);
      UserDetailsEntity userDetails = optUserDetails.get();

      Query<ParticipantStudyEntity> query =
          session.createQuery(
              "from ParticipantStudyEntity where userDetails = :userDetails order by status asc");

      query.setParameter("userDetails", userDetails);
      participantStudiesList = query.getResultList();
      // Remove duplicate participant study records when participant study created for multiple
      // sites. All the participant study records will be in withdrawn status except one.
      Map<String, ParticipantStudyEntity> map = new HashedMap<>();
      for (ParticipantStudyEntity participantStudy : participantStudiesList) {
        if (!map.containsKey(participantStudy.getStudyId())) {
          map.put(participantStudy.getStudyId(), participantStudy);
        }
      }
      return map.values().stream().collect(Collectors.toList());
    } else {
      return null;
    }
  }
}
