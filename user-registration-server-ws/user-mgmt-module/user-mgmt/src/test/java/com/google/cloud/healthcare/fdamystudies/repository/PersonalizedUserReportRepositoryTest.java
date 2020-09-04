/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import static com.google.cloud.healthcare.fdamystudies.matchers.HasReport.hasReport;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import com.google.cloud.healthcare.fdamystudies.model.PersonalizedUserReportEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.GregorianCalendar;
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
public class PersonalizedUserReportRepositoryTest {

  @Resource private PersonalizedUserReportRepository personalizedUserReportRepository;

  @Resource private UserDetailsRepository userDetailsRepository;

  @Resource private StudyRepository studyInfoRepository;

  @Test
  public void LooksUpMostRecentReportForUserAndStudy() {
    UserDetailsEntity user1 =
        UserDetailsEntity.builder()
            .userId("user_id1")
            .email("email1@example.com")
            .firstName("First name1")
            .lastName("Last name1")
            .verificationDate(
                new Timestamp((new GregorianCalendar(2000, 1, 2).getTime()).getTime()))
            .codeExpireDate(Timestamp.valueOf(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0)))
            .build();
    UserDetailsEntity user2 =
        UserDetailsEntity.builder()
            .userId("user_id2")
            .email("email2@example.com")
            .firstName("First name2")
            .lastName("Last name2")
            .verificationDate(
                new Timestamp((new GregorianCalendar(2000, 1, 2).getTime()).getTime()))
            .codeExpireDate(Timestamp.valueOf(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0)))
            .build();
    userDetailsRepository.save(user1);
    userDetailsRepository.save(user2);

    StudyEntity study1 = StudyEntity.builder().customId("study 1").name("Study 1").build();

    StudyEntity study2 = StudyEntity.builder().customId("study 2").name("Study 2").build();
    studyInfoRepository.save(study1);
    studyInfoRepository.save(study2);

    // This is the report that should be retrieved.
    PersonalizedUserReportEntity report1 =
        PersonalizedUserReportEntity.builder()
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content 1 for user 1")
            .build();
    PersonalizedUserReportEntity report2 =
        PersonalizedUserReportEntity.builder()
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content 2 for user 1")
            .build();
    PersonalizedUserReportEntity report3 =
        PersonalizedUserReportEntity.builder()
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 2")
            .reportContent("Report 2 content for user 1")
            .build();
    PersonalizedUserReportEntity report4 =
        PersonalizedUserReportEntity.builder()
            .userDetails(user2)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content for user 2")
            .build();
    PersonalizedUserReportEntity report5 =
        PersonalizedUserReportEntity.builder()
            .userDetails(user1)
            .studyInfo(study2)
            .reportTitle("Report 2")
            .reportContent("Report 2 content for user 1")
            .build();
    personalizedUserReportRepository.save(report1);
    personalizedUserReportRepository.save(report2);
    personalizedUserReportRepository.save(report3);
    personalizedUserReportRepository.save(report4);
    personalizedUserReportRepository.save(report5);

    assertThat(
        personalizedUserReportRepository.findByUserDetailsUserIdAndStudyInfoCustomId(
            "user_id1", "study 1"),
        containsInAnyOrder(
            hasReport("Report 1", "Report 1 content 1 for user 1"),
            hasReport("Report 1", "Report 1 content 2 for user 1"),
            hasReport("Report 2", "Report 2 content for user 1")));
  }
}
