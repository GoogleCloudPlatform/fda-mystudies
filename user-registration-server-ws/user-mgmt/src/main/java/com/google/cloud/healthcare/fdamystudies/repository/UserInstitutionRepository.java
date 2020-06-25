package com.google.cloud.healthcare.fdamystudies.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.healthcare.fdamystudies.model.UserInstitution;

@Repository
@Transactional
public interface UserInstitutionRepository extends JpaRepository<UserInstitution, Long> {
  Optional<UserInstitution> findByUserUserId(String userId);
}
