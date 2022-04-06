/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
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
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.HibernateTemplate;
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
          if (exportedUserRecord.isDisabled()
              & StringUtils.isNotBlank(exportedUserRecord.getEmail())) {
            gciDisabledEmail.add(exportedUserRecord.getEmail());
          }
          gciEmail.add(exportedUserRecord.getEmail());
        }

        List<String> deletedGciUsers = ListUtils.removeAll(userEmail, gciEmail);
        List<String> disableUsers = new ArrayList<>();
        disableUsers.addAll(deletedGciUsers);
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
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("addorUpdateOrgUserInfo  - Ends");
  }
}
