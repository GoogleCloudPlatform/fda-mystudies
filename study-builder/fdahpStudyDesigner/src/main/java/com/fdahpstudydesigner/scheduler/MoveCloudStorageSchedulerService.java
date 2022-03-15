/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.scheduler;

import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.dao.StudyDAOImpl;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableScheduling
public class MoveCloudStorageSchedulerService {

  private static Logger logger = Logger.getLogger(MoveCloudStorageSchedulerService.class.getName());

  @Autowired StudyDAOImpl study;

  @Value("${jobs.move.cloud.storage.scheduler.enable}")
  private boolean moveCloudStorageSchedulerEnable;

  @Bean()
  public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(2);
    return taskScheduler;
  }

  HibernateTemplate hibernateTemplate;

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  @Scheduled(
      fixedDelayString = "${move.cloud.storage.fixed.delay.ms}",
      initialDelayString = "${move.cloud.storage.initial.delay.ms}")
  public void moveCloudStorageStructure() {
    logger.info("moveCloudStorageStructure  - Starts");

    try {

      if (moveCloudStorageSchedulerEnable) {

        Session session = hibernateTemplate.getSessionFactory().openSession();
        // LIMIT = 10 add order by SBO.createdOn desc
        List<StudyBo> studyBoList =
            session
                .createQuery(
                    "FROM StudyBo SBO WHERE SBO.live = 0 and SBO.isCloudStorageMoved=0 and SBO.customStudyId IS NOT NULL order by SBO.createdOn desc")
                .list();

        for (StudyBo studyBo : studyBoList) {

          session
              .createQuery(
                  "update StudyBo SBO set SBO.isCloudStorageMoved = 1 where SBO.customStudyId=:customStudyId")
              .setString("customStudyId", studyBo.getCustomStudyId())
              .executeUpdate();

          study.moveOrCopyCloudStorage(session, studyBo, true, true, null);

          session
              .createQuery(
                  "update StudyBo SBO set SBO.isCloudStorageMoved = 2 where SBO.customStudyId=:customStudyId")
              .setString("customStudyId", studyBo.getCustomStudyId())
              .executeUpdate();
        }
      }
    } catch (Exception e) {
      logger.error("moveCloudStorageStructure  - ERROR", e.getCause());
      e.printStackTrace();
    }
    logger.info("moveCloudStorageStructure  - Ends");
  }
}
