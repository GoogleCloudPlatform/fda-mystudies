/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import com.google.cloud.healthcare.fdamystudies.beans.UserResourceBean;
import com.google.cloud.healthcare.fdamystudies.repository.PersonalizedUserReportRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyInfoRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.PersonalizedUserReportBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Ignore;
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

// TODO (#761) Added @Ignore to test classes written by UNC team, should be fixed later or next
// track

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
@Ignore
public class PersonalizedUserReportServiceTest {

  @Resource private PersonalizedUserReportRepository personalizedUserReportRepository;

  @Resource private UserDetailsRepository userDetailsRepository;

  @Resource private StudyInfoRepository studyInfoRepository;

  @Autowired private PersonalizedUserReportService personalizedUserReportService;
  @Autowired private ApplicationContext ctx;

  private static final UserResourceBean.ResourceType resourceType =
      UserResourceBean.ResourceType.PERSONALIZED_REPORT;

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
            .id(1)
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content 1 for user 1")
            .creationTime(new Timestamp(1590031332001L))
            .build();
    PersonalizedUserReportBO report2 =
        PersonalizedUserReportBO.builder()
            .id(2)
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content 2 for user 1")
            .creationTime(new Timestamp(1590031332002L))
            .build();
    PersonalizedUserReportBO report3 =
        PersonalizedUserReportBO.builder()
            .id(3)
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content 3 for user 1")
            .creationTime(new Timestamp(1590031332003L))
            .build();
    PersonalizedUserReportBO report4 =
        PersonalizedUserReportBO.builder()
            .id(4)
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 3")
            .reportContent("Report 3 content for user 1")
            .creationTime(new Timestamp(1590031332004L))
            .build();
    PersonalizedUserReportBO report5 =
        PersonalizedUserReportBO.builder()
            .id(5)
            .userDetails(user2)
            .studyInfo(study1)
            .reportTitle("Report 1")
            .reportContent("Report 1 content for user 2")
            .creationTime(new Timestamp(1590031332005L))
            .build();
    PersonalizedUserReportBO report6 =
        PersonalizedUserReportBO.builder()
            .id(6)
            .userDetails(user1)
            .studyInfo(study2)
            .reportTitle("Report 3")
            .reportContent("Report 3 content for user 1")
            .creationTime(new Timestamp(1590031332006L))
            .build();
    PersonalizedUserReportBO report7 =
        PersonalizedUserReportBO.builder()
            .id(7)
            .userDetails(user1)
            .studyInfo(study1)
            .reportTitle("Report 2")
            .reportContent("Report 2 content for user 1")
            .creationTime(new Timestamp(1590031332007L))
            .build();
    personalizedUserReportRepository.save(report1);
    personalizedUserReportRepository.save(report2);
    personalizedUserReportRepository.save(report3);
    personalizedUserReportRepository.save(report4);
    personalizedUserReportRepository.save(report5);
    personalizedUserReportRepository.save(report6);
    personalizedUserReportRepository.save(report7);

    List<UserResourceBean> userReports =
        personalizedUserReportService.getLatestPersonalizedUserReports("user_id1", "study 1");
    assertThat(userReports, hasSize(3));
    // Reports are sorted in descending order by creation timestamp (newest first).
    assertThat(
        userReports,
        contains(
            equalTo(
                new UserResourceBean("Report 2", "Report 2 content for user 1", resourceType, "7")),
            equalTo(
                new UserResourceBean("Report 3", "Report 3 content for user 1", resourceType, "4")),
            equalTo(
                new UserResourceBean(
                    "Report 1", "Report 1 content 3 for user 1", resourceType, "3"))));
  }
}
