/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;

import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserInstitution;
import java.util.Optional;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

// TODO (#761) Added @Ignore to test classes written by UNC team, should be fixed later or next
// track

@RunWith(SpringRunner.class)
@DataJpaTest
@Ignore
public class UserInstitutionRepositoryTest {
  private static Logger logger = LoggerFactory.getLogger(UserInstitutionRepositoryTest.class);
  @Autowired private UserDetailsBORepository userRepository;
  @Autowired private UserInstitutionRepository institutionRepository;

  @Test
  public void saveUserInstitution() {
    UserDetailsBO user1 = new UserDetailsBO();
    user1.setUserId("user_id");
    user1 = userRepository.save(user1);
    institutionRepository.save(
        UserInstitution.builder().user(user1).institutionId("fake_institution").build());
    UserDetailsBO user2 = new UserDetailsBO();
    user2.setUserId("user_id2");
    user2 = userRepository.save(user2);
    Optional<UserInstitution> institution = institutionRepository.findByUserUserId("user_id");
    assertTrue(institution.isPresent());
    assertThat(institution.get().getUser(), equalTo(user1));
    assertThat(institution.get().getInstitutionId(), equalTo("fake_institution"));
  }

  @Test
  public void deletesUserInstitutionIfUserIsDeleted() {
    UserDetailsBO user = new UserDetailsBO();
    user.setUserId("fake_user_2");
    user = userRepository.save(user);
    institutionRepository.save(
        UserInstitution.builder().user(user).institutionId("fake_institution_2").build());
    assertThat(institutionRepository.findAll(), hasSize(1));
    userRepository.delete(user);
    userRepository.flush();
    assertThat(institutionRepository.findAll(), hasSize(0));
  }
}
