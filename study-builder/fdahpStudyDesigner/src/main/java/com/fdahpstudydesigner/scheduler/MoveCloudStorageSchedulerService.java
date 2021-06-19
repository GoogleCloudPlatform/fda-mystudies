/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
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
                    "FROM StudyBo SBO WHERE SBO.live = 0 and SBO.isCloudStorageMoved=0 order by SBO.createdOn desc")
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
