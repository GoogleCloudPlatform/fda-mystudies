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
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.PersonalizedUserReportBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
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

  @Resource private StudyInfoRepository studyInfoRepository;

  @Test
  public void LooksUpMostRecentReportForUserAndStudy() {
    UserDetailsBO user1 =
        UserDetailsBO.builder()
            .userId("user_id1")
            .email("email1@example.com")
            .firstName("First name1")
            .lastName("Last name1")
            ._ts(new GregorianCalendar(2000, 1, 1).getTime())
            .verificationDate(new GregorianCalendar(2000, 1, 2).getTime())
            .codeExpireDate(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0))
            .build();
    UserDetailsBO user2 =
        UserDetailsBO.builder()
            .userId("user_id2")
            .email("email2@example.com")
            .firstName("First name2")
            .lastName("Last name2")
            ._ts(new GregorianCalendar(2000, 1, 1).getTime())
            .verificationDate(new GregorianCalendar(2000, 1, 2).getTime())
            .codeExpireDate(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0))
            .build();
    userDetailsRepository.save(user1);
    userDetailsRepository.save(user2);

    StudyInfoBO study1 = StudyInfoBO.builder().customId("study 1").name("Study 1").build();

    StudyInfoBO study2 = StudyInfoBO.builder().customId("study 2").name("Study 2").build();
    studyInfoRepository.save(study1);
    studyInfoRepository.save(study2);

    // This is the report that should be retrieved.
    PersonalizedUserReportBO report1 =
        PersonalizedUserReportBO.builder()
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content 1 for user 1")
            .build();
    PersonalizedUserReportBO report2 =
        PersonalizedUserReportBO.builder()
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content 2 for user 1")
            .build();
    PersonalizedUserReportBO report3 =
        PersonalizedUserReportBO.builder()
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 2")
            .reportContent("Report 2 content for user 1")
            .build();
    PersonalizedUserReportBO report4 =
        PersonalizedUserReportBO.builder()
            .userDetails(user2)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content for user 2")
            .build();
    PersonalizedUserReportBO report5 =
        PersonalizedUserReportBO.builder()
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
