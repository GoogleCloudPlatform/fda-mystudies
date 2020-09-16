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
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantStudiesInfoDaoImpl implements ParticipantStudiesInfoDao {

  private static final Logger logger = LoggerFactory.getLogger(ParticipantStudiesInfoDaoImpl.class);

  @Autowired private SessionFactory sessionFactory;

  @Autowired private UserDetailsRepository userDetailsRepository;

  @Override
  @SuppressWarnings("unchecked")
  public List<ParticipantStudyEntity> getParticipantStudiesInfo(String userDetailsId) {

    List<ParticipantStudyEntity> participantStudiesList = null;
    logger.info("(DAO)...ParticipantStudiesInfoDaoImpl.getParticipantStudiesInfo()...Started");
    if (userDetailsId != null) {
      Session session = this.sessionFactory.getCurrentSession();

      Optional<UserDetailsEntity> optUserDetails =
          userDetailsRepository.findByUserId(userDetailsId);
      UserDetailsEntity userDetails = optUserDetails.get();

      Query<ParticipantStudyEntity> query =
          session.createQuery("from ParticipantStudyEntity where userDetails = :userDetails");

      query.setParameter("userDetails", userDetails);
      participantStudiesList = query.getResultList();
      return participantStudiesList;

    } else {
      logger.info("(DAO)...ParticipantStudiesInfoDaoImpl.getParticipantStudiesInfo()...Ended");
      return null;
    }
  }
}
