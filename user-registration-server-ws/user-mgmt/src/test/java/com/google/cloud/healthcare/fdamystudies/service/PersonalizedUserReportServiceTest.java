/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import com.google.cloud.healthcare.fdamystudies.beans.UserResourceBean;
import com.google.cloud.healthcare.fdamystudies.model.PersonalizedUserReportBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.repository.PersonalizedUserReportRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyInfoRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.GregorianCalendar;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
public class PersonalizedUserReportServiceTest {

  @Resource private PersonalizedUserReportRepository personalizedUserReportRepository;

  @Resource private UserDetailsRepository userDetailsRepository;

  @Resource private StudyInfoRepository studyInfoRepository;

  @Autowired private PersonalizedUserReportService personalizedUserReportService;
  @Autowired private ApplicationContext ctx;

  @Test
  public void GetsMostRecentReportsForUserAndStudy() {
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
            .reportTitle("Report 1")
            .reportContent("Report 1 content 3 for user 1")
            .build();
    PersonalizedUserReportBO report4 =
        PersonalizedUserReportBO.builder()
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 2")
            .reportContent("Report 2 content for user 1")
            .build();
    PersonalizedUserReportBO report5 =
        PersonalizedUserReportBO.builder()
            .userDetails(user2)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content for user 2")
            .build();
    PersonalizedUserReportBO report6 =
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
    personalizedUserReportRepository.save(report6);

    assertThat(
        personalizedUserReportService.getLatestPersonalizedUserReports("user_id1", "study 1"),
        containsInAnyOrder(
            equalTo(new UserResourceBean("Report 2", "Report 2 content for user 1")),
            equalTo(new UserResourceBean("Report 1", "Report 1 content 3 for user 1"))));
  }
}
