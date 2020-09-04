/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import static com.google.cloud.healthcare.fdamystudies.matchers.HasLastName.hasLastName;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// TODO (#761) Added @Ignore to test classes written by UNC team, should be fixed later or next
// track

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
@Ignore
public class UserDetailsRepositoryTest {

  @Resource private UserDetailsRepository userDetailsRepository;

  @Test
  public void FindsUsersWithLastName() {
    UserDetailsEntity user1 =
        UserDetailsEntity.builder()
            .userId("user_id")
            .email("email1@example.com")
            .firstName("Given name")
            .lastName("Surname")
            .verificationDate(
                new Timestamp((new GregorianCalendar(2000, 1, 2).getTime()).getTime()))
            .codeExpireDate(Timestamp.valueOf(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0)))
            .build();
    UserDetailsEntity user2 =
        UserDetailsEntity.builder()
            .userId("user_id")
            .email("email2@example.com")
            .firstName("Given name 2")
            .lastName("Surname")
            .verificationDate(
                new Timestamp((new GregorianCalendar(2000, 1, 2).getTime()).getTime()))
            .codeExpireDate(Timestamp.valueOf(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0)))
            .build();
    UserDetailsEntity user3 =
        UserDetailsEntity.builder()
            .userId("user_id")
            .email("email2@example.com")
            .firstName("Given name 2")
            .lastName("NotSurname")
            .verificationDate(
                new Timestamp((new GregorianCalendar(2000, 1, 2).getTime()).getTime()))
            .codeExpireDate(Timestamp.valueOf(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0)))
            .build();
    userDetailsRepository.save(user1);
    userDetailsRepository.save(user2);
    userDetailsRepository.save(user3);

    List<UserDetailsEntity> users = userDetailsRepository.findByLastName("Surname");
    assertThat(users, hasItems(hasLastName("Surname"), hasLastName("Surname")));
  }
}
