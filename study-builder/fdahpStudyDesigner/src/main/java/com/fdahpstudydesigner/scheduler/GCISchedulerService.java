/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.scheduler;

import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.dao.UsersDAO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ListUsersPage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableScheduling
public class GCISchedulerService {

  private static Logger logger = Logger.getLogger(GCISchedulerService.class.getName());

  Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();
  String gciEnabled = configMap.get("gciEnabled");

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

  @Autowired private UsersDAO usersDAO;

  @Scheduled(cron = "0 * * * * ?")
  public void addorUpdateOrgUserInfo() {
    logger.info("addorUpdateOrgUserInfo  - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      List<UserBO> gciUserList = usersDAO.getGciUserList();
      List<String> userEmail = new ArrayList<>();
      List<String> gciEmail = new ArrayList<>();
      List<String> gciDisabledEmail = new ArrayList<>();
      if (Boolean.parseBoolean(gciEnabled)) {
        for (UserBO user : gciUserList) {
          userEmail.add(user.getUserEmail());
        }
        // Start listing users from the beginning, 1000 at a time.
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
        for (ExportedUserRecord exportedUserRecord : page.iterateAll()) {
          if (exportedUserRecord.isDisabled()) {
            gciDisabledEmail.add(exportedUserRecord.getEmail());
          }
          gciEmail.add(exportedUserRecord.getEmail());
        }

        List<String> deletedGciUser = ListUtils.removeAll(userEmail, gciEmail);
        List<String> disableUsers = new ArrayList<>();
        disableUsers.addAll(deletedGciUser);
        disableUsers.addAll(gciDisabledEmail);

        for (UserBO user : gciUserList) {
          for (String disableUser : disableUsers) {
            if (user.getUserEmail().equals(disableUser)) {
              session
                  .createSQLQuery(
                      "Update users set force_logout='Y', status=0 WHERE email =:userEmail")
                  .setParameter("userEmail", user.getUserEmail())
                  .executeUpdate();
            }
          }
        }
      } else {
        if (!gciUserList.isEmpty()) {
          for (UserBO user : gciUserList) {
            session
                .createSQLQuery(
                    "Update users set force_logout='Y', status=0 WHERE email =:userEmail")
                .setParameter("userEmail", user.getUserEmail())
                .executeUpdate();
          }
        }
      }
    } catch (Exception e) {
      logger.error("addorUpdateOrgUserInfo  - ERROR", e.getCause());
      e.printStackTrace();
    }
    logger.info("addorUpdateOrgUserInfo  - Ends");
  }
}
