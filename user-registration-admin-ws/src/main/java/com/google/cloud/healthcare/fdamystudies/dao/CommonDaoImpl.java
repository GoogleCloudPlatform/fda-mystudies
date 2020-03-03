/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;
import com.google.cloud.healthcare.fdamystudies.model.MailMessages;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

@Repository
public class CommonDaoImpl implements CommonDao {
  private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired private ApplicationConfiguratation applicationConfiguratation;

  Transaction transaction = null;

  @SuppressWarnings("unchecked")
  public List<SitePermission> getSitePermissions(Integer userId) {
    logger.info(" CommonDaoImpl - getSites():starts");
    List<SitePermission> sitePermission = new ArrayList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      sitePermission =
          session
              .createQuery("from SitePermission where urAdminUser.id=:userId")
              .setParameter("userId", userId)
              .getResultList();
    } catch (Exception e) {
      logger.info("CommonDaoImpl - getSites() : error", e);
    }
    logger.info("CommonDaoImpl - getSites() : ends");

    return sitePermission;
  }

  @SuppressWarnings("unchecked")
  public List<SiteBo> getAllSite() {
    logger.info(" CommonDaoImpl - getAllSite():starts");
    List<SiteBo> sites = new ArrayList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

      sites = session.createQuery("from SiteBo").getResultList();
    } catch (Exception e) {
      logger.info("CommonDaoImpl - getAllSite() : error", e);
    }

    logger.info("CommonDaoImpl - getAllSite() : ends");

    return sites;
  }

  @SuppressWarnings("unchecked")
  public List<ParticipantRegistrySite> getParticipantRegistryOfSites(List<Integer> siteIds) {
    logger.info(" SiteDaoImpl - getParticipantRegistryOfSites():starts");
    List<ParticipantRegistrySite> participantRegistry = new ArrayList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      participantRegistry =
          session
              .createQuery("from ParticipantRegistrySite where sites.id in (:siteIds)")
              .setParameterList("siteIds", siteIds)
              .getResultList();
    } catch (Exception e) {
      logger.info("SiteDaoImpl - getParticipantRegistryOfSites() : error", e);
    }

    logger.info("SiteDaoImpl - getParticipantRegistryOfSites() : ends");
    return participantRegistry;
  }

  public ParticipantStudiesBO getParticipantStudiesBOs(Integer participantRegistryId) {
    logger.info("CommonDaoImpl - getParticipantStudiesBOs() : starts");
    ParticipantStudiesBO participantStudiesBO = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      participantStudiesBO =
          (ParticipantStudiesBO)
              session
                  .createQuery(
                      "from ParticipantStudiesBO where participantRegistrySite.id = :participantRegistryId")
                  .setParameter("participantRegistryId", participantRegistryId)
                  .getSingleResult();
    } catch (Exception e) {
      logger.info("CommonDaoImpl - getParticipantStudiesBOs() : error");
    }
    logger.info("CommonDaoImpl - getParticipantStudiesBOs() : ends");
    return participantStudiesBO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ParticipantRegistrySite> getParticipantRegistry(Integer studyId, String email) {
    logger.info("CommonDaoImpl - getParticipantRegistry() : starts");
    List<ParticipantRegistrySite> participantRegistry = new LinkedList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query<ParticipantRegistrySite> query =
          session
              .createQuery(
                  "from ParticipantRegistrySite where studyInfo.id = :studyId and email = :email")
              .setParameter("studyId", studyId)
              .setParameter("email", email);
      participantRegistry = query.getResultList();
    } catch (Exception e) {
      logger.error("CommonDaoImpl - getParticipantRegistry() : error");
    }
    logger.info("CommonDaoImpl - getParticipantRegistry() : ends");
    return participantRegistry;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ParticipantStudiesBO> getParticipantsEnrollmentsOfSites(List<Integer> usersSiteIds) {
    logger.info("CommonDaoImpl - getParticipantRegistry() : starts");
    List<ParticipantStudiesBO> participantsEnrollmentsOfSites = new LinkedList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query<ParticipantStudiesBO> query =
          session
              .createQuery("from ParticipantStudiesBO where siteBo.id in (:siteIds)")
              .setParameterList("siteIds", usersSiteIds);
      participantsEnrollmentsOfSites = query.getResultList();
    } catch (Exception e) {
      logger.error("CommonDaoImpl - getParticipantRegistry() : error");
    }
    logger.info("CommonDaoImpl - getParticipantRegistry() : ends");
    return participantsEnrollmentsOfSites;
  }

  @Override
  public String addToMailMessages(List<MailMessages> mailMessages) {
    logger.info("CommonDaoImpl - processEmail() starts");
    String message = AppConstants.FAILURE;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      for (MailMessages messages : mailMessages) {
        session.save(messages);
      }
      transaction.commit();
      message = AppConstants.SUCCESS;
    } catch (Exception e) {
      if (null != transaction) {
        transaction.rollback();
      }
      logger.error("CommonDaoImpl - processEmail() error", e);
    }
    logger.info("CommonDaoImpl - processEmail() end");
    return message;
  }

  @Override
  public void processEmail() {
    logger.info("CommonDaoImpl - processEmail() starts");
    List<MailMessages> mailMessageList = new ArrayList<>();
    List<Integer> mailIdList = new ArrayList<>();

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<MailMessages> criteriaQuery = builder.createQuery(MailMessages.class);
      Root<MailMessages> bulkMessage = criteriaQuery.from(MailMessages.class);
      criteriaQuery
          .select(bulkMessage)
          .where(builder.equal(bulkMessage.get("isEmailSent"), AppConstants.ZERO))
          .orderBy(builder.asc(bulkMessage.get("mailMessageId")));
      mailMessageList = session.createQuery(criteriaQuery).setMaxResults(120).getResultList();

      if (!mailMessageList.isEmpty()) {
        mailIdList = sendMail(mailMessageList);
        if (!mailIdList.isEmpty()) {
          updateBulkResetStatus(mailIdList, session);
        }
      }

    } catch (Exception e) {
      logger.error("ERROR ::: CommonDaoImpl - processEmail() =", e);
    }
    logger.info("CommonDaoImpl - processEmail() ends");
  }

  public List<Integer> sendMail(List<MailMessages> mailMessages) {
    List<Integer> mailIdList = new ArrayList<>();
    logger.debug("sendEmail()====start");
    javax.mail.Session session = null;
    try {
      final String userName = applicationConfiguratation.getFromEmailAddress();
      final String password = applicationConfiguratation.getFromEmailPasswod();
      Properties properties = new Properties();
      properties.put(AppConstants.MAIL_SMTP_HOST, applicationConfiguratation.getSmtpHostName());
      properties.put(AppConstants.MAIL_SMTP_PORT, applicationConfiguratation.getSmtpPortValue());

      if (null != applicationConfiguratation.getAppEnv()
          && AppConstants.APP_ENV_LOCAL.equals(applicationConfiguratation.getAppEnv())) {
        properties.put(AppConstants.MAIL_SMTP_AUTH, "true");
        properties.put(
            AppConstants.MAIL_SMTP_SOCKETFACTROY_PORT,
            applicationConfiguratation.getSmtpPortValue());
        properties.put(
            AppConstants.MAIL_SMTP_SOCKETFACTROY_CLASS,
            applicationConfiguratation.getSslFactoryValue());
        session =
            javax.mail.Session.getInstance(
                properties,
                new javax.mail.Authenticator() {
                  @Override
                  protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                  }
                });
      } else {
        properties.put(AppConstants.MAIL_SMTP_AUTH, "false");
        session = javax.mail.Session.getInstance(properties);
      }
      if (!mailMessages.isEmpty()) {
        Message message = null;
        InternetAddress inetAddress = new InternetAddress(userName);
        for (MailMessages mailMessage : mailMessages) {
          try {
            message = new MimeMessage(session);
            message.setFrom(inetAddress);
            if (StringUtils.isNotBlank(mailMessage.getEmailId())) {
              message.setRecipients(
                  Message.RecipientType.TO, InternetAddress.parse(mailMessage.getEmailId()));
            }
            message.setSubject(mailMessage.getEmailTitle());
            message.setContent(mailMessage.getEmailBody(), "text/html");
            if (null != mailMessage.getCcEmail() && !mailMessage.getCcEmail().isEmpty()) {
              message.setRecipients(
                  Message.RecipientType.CC,
                  InternetAddress.parse(mailMessage.getCcEmail().toLowerCase()));
            }
            if (null != mailMessage.getBccEmail() && !mailMessage.getBccEmail().isEmpty()) {
              message.setRecipients(
                  Message.RecipientType.BCC,
                  InternetAddress.parse(mailMessage.getBccEmail().toLowerCase()));
            }
            Transport.send(message);
            mailIdList.add(mailMessage.getMailMessageId());
            logger.info(
                "Email Sent Successfully to : {}, Name : {}",
                mailMessage.getEmailId(),
                mailMessage.getUserName());
          } catch (Exception e) {
            logger.error(
                "CommonDaoImpl - sendEmail()- error() Unable to send email to this Id : "
                    + mailMessage.getEmailId(),
                e);
          }
        }
      }
      logger.debug("sendEmail()====end");
    } catch (Exception e) {
      logger.error("ERROR ::::sendEmail()", e);
    }

    logger.info("Mail.sendEmail() :: Ends");
    return mailIdList;
  }

  public void updateBulkResetStatus(List<Integer> mailIdList, Session session) {
    logger.info("CommonDaoImpl - updateBulkResetStatus() starts");
    try {
      transaction = session.beginTransaction();
      Query query =
          session.createSQLQuery(
              "update mail_messages u set u.is_email_sent=:emailSent, u.sent_Datetime=:sentTime where"
                  + " u.mail_messageid IN (:messageIdList)");
      query.setParameter("emailSent", 1);
      query.setParameter("sentTime", new Date());
      query.setParameterList("messageIdList", mailIdList);
      query.executeUpdate();
      transaction.commit();
    } catch (Exception e) {
      if (null != transaction) {
        transaction.rollback();
      }

      logger.error("ERROR ::: CommonDaoImpl - updateBulkResetStatus() =", e);
    }

    logger.info("CommonDaoImpl - updateBulkResetStatus() ends");
  }
}
