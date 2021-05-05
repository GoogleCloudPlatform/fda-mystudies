/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
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

package com.fdahpstudydesigner.dao;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ACTIVE_TASK_DELETED;

import com.fdahpstudydesigner.bean.ActiveStatisticsBean;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.ActiveTaskAtrributeValuesBo;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.ActiveTaskListBo;
import com.fdahpstudydesigner.bo.ActiveTaskMasterAttributeBo;
import com.fdahpstudydesigner.bo.ActivetaskFormulaBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.StatisticImageListBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import com.fdahpstudydesigner.bo.StudyVersionBo;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyActiveTasksDAOImpl implements StudyActiveTasksDAO {

  private static XLogger logger =
      XLoggerFactory.getXLogger(StudyActiveTasksDAOImpl.class.getName());
  @Autowired private HttpServletRequest request;
  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;
  HibernateTemplate hibernateTemplate;
  private Query query = null;
  String queryString = "";
  private Transaction transaction = null;

  @Autowired private AuditLogDAO auditLogDAO;

  public StudyActiveTasksDAOImpl() {}

  @Override
  public String deleteActiveTask(
      ActiveTaskBo activeTaskBo, SessionObject sesObj, String customStudyId) {
    logger.entry("begin deleteActiveTAsk()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    StudyVersionBo studyVersionBo = null;
    String deleteActQuery = "";
    StudyBuilderAuditEvent eventEnum = null;
    Map<String, String> values = new HashMap<String, String>();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      auditRequest.setStudyId(customStudyId);
      session = hibernateTemplate.getSessionFactory().openSession();
      if (activeTaskBo != null) {
        Integer studyId = activeTaskBo.getStudyId();

        transaction = session.beginTransaction();

        query =
            session
                .getNamedQuery("StudyBo.getStudyBycustomStudyId")
                .setString("customStudyId", customStudyId);
        query.setMaxResults(1);
        StudyBo study = (StudyBo) query.uniqueResult();
        auditRequest.setStudyVersion(study.getVersion().toString());
        auditRequest.setAppId(study.getAppId());

        queryString =
            "DELETE From NotificationBO where activeTaskId=:activeId AND notificationSent=false";
        session
            .createQuery(queryString)
            .setParameter("activeId", activeTaskBo.getId())
            .executeUpdate();
        query =
            session
                .getNamedQuery("getStudyByCustomStudyId")
                .setString("customStudyId", customStudyId);
        query.setMaxResults(1);
        studyVersionBo = (StudyVersionBo) query.uniqueResult();
        // get the study version table to check whether study launch or
        // not ,
        // if record exist in version table, then study already launch ,
        // so do soft delete active task
        // if record does not exist , then study has not launched , so
        // do hard delete active task
        if (studyVersionBo != null) {
          // soft delete active task after study launch

          deleteActQuery =
              "update ActiveTaskAtrributeValuesBo set active=0 where activeTaskId= :activeTaskId";

          query =
              session
                  .createQuery(deleteActQuery)
                  .setParameter("activeTaskId", activeTaskBo.getId());
          query.executeUpdate();

          session
              .createQuery(
                  "update ActiveTaskBo set active=0 ,modifiedBy=:userId"
                      + ",modifiedDate= :currentDateTime ,customStudyId=:customStudyId where id=:activeTaskId")
              .setParameter("userId", sesObj.getUserId())
              .setParameter("currentDateTime", FdahpStudyDesignerUtil.getCurrentDateTime())
              .setParameter("customStudyId", customStudyId)
              .setParameter("activeTaskId", activeTaskBo.getId())
              .executeUpdate();

          values.put("activetask_id", activeTaskBo.getShortTitle());
          eventEnum = STUDY_ACTIVE_TASK_DELETED;
        } else {
          // hard delete active task before study launch
          session
              .createSQLQuery(
                  "DELETE FROM active_task_frequencies WHERE active_task_id=:activeTaskId")
              .setParameter("activeTaskId", activeTaskBo.getId())
              .executeUpdate();
          session
              .createSQLQuery(
                  "DELETE FROM active_task_custom_frequencies WHERE active_task_id =:activeTaskId")
              .setParameter("activeTaskId", activeTaskBo.getId())
              .executeUpdate();

          deleteActQuery = "delete ActiveTaskAtrributeValuesBo where activeTaskId=:activeTaskId";

          query =
              session
                  .createQuery(deleteActQuery)
                  .setParameter("activeTaskId", activeTaskBo.getId());
          query.executeUpdate();

          session
              .createQuery("delete ActiveTaskBo where id =:activeTaskId")
              .setParameter("activeTaskId", activeTaskBo.getId())
              .executeUpdate();

          values.put("activetask_id", activeTaskBo.getShortTitle());
          eventEnum = STUDY_ACTIVE_TASK_DELETED;
        }

        query =
            session
                .createQuery(
                    " UPDATE StudySequenceBo SET studyExcActiveTask =false WHERE studyId = :studyId")
                .setParameter("studyId", studyId);
        query.executeUpdate();

        message = FdahpStudyDesignerConstants.SUCCESS;
        auditLogEventHelper.logEvent(eventEnum, auditRequest, values);

        transaction.commit();
      }
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyActiveTasksDAOImpl - deleteActiveTAsk() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("deleteActiveTAsk() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ActiveTaskBo getActiveTaskById(Integer activeTaskId, String customStudyId) {
    logger.entry("begin getActiveTaskById()");
    ActiveTaskBo activeTaskBo = null;
    Session session = null;
    List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      activeTaskBo = (ActiveTaskBo) session.get(ActiveTaskBo.class, activeTaskId);
      if (activeTaskBo != null) {
        query =
            session
                .createQuery("from ActiveTaskAtrributeValuesBo where activeTaskId=:activeTaskId")
                .setParameter("activeTaskId", activeTaskBo.getId());
        activeTaskAtrributeValuesBos = query.list();
        if (StringUtils.isNotEmpty(customStudyId)) {
          // to check duplicate short title of active task
          BigInteger shortTitleCount =
              (BigInteger)
                  session
                      .createSQLQuery(
                          "select count(*) from active_task a "
                              + "where a.short_title=:title and custom_study_id=:customStudyId"
                              + " and a.active=1 and a.is_live=1")
                      .setParameter("title", activeTaskBo.getShortTitle())
                      .setParameter("customStudyId", customStudyId)
                      .uniqueResult();
          if ((shortTitleCount != null) && (shortTitleCount.intValue() > 0)) {
            activeTaskBo.setIsDuplicate(shortTitleCount.intValue());
          } else {
            activeTaskBo.setIsDuplicate(0);
          }
        } else {
          activeTaskBo.setIsDuplicate(0);
        }

        if ((activeTaskAtrributeValuesBos != null) && !activeTaskAtrributeValuesBos.isEmpty()) {
          for (ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo :
              activeTaskAtrributeValuesBos) {
            if (StringUtils.isNotEmpty(customStudyId)) {
              // to check duplicate short title in dashboard of
              // active task
              BigInteger statTitleCount =
                  (BigInteger)
                      session
                          .createSQLQuery(
                              "select count(*) from active_task_attrtibutes_values at "
                                  + "where at.identifier_name_stat=:identifierNameStat "
                                  + "and  at.active_task_id in "
                                  + "(select a.id from active_task a where a.custom_study_id=:customStudyId"
                                  + " and a.active=1 and a.is_live=1)")
                          .setParameter("customStudyId", customStudyId)
                          .setParameter(
                              "identifierNameStat",
                              activeTaskAtrributeValuesBo.getIdentifierNameStat())
                          .uniqueResult();
              if ((statTitleCount != null) && (statTitleCount.intValue() > 0)) {
                activeTaskAtrributeValuesBo.setIsIdentifierNameStatDuplicate(
                    statTitleCount.intValue());
              } else {
                activeTaskAtrributeValuesBo.setIsIdentifierNameStatDuplicate(0);
              }
            } else {
              activeTaskAtrributeValuesBo.setIsIdentifierNameStatDuplicate(0);
            }
          }
          activeTaskBo.setTaskAttributeValueBos(activeTaskAtrributeValuesBos);
        }

        String searchQuery = "";
        if (null != activeTaskBo.getFrequency()) {
          if (activeTaskBo
              .getFrequency()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
            searchQuery =
                "From ActiveTaskCustomScheduleBo ATSBO where ATSBO.activeTaskId=:activeTaskId";
            query =
                session.createQuery(searchQuery).setParameter("activeTaskId", activeTaskBo.getId());
            List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleBos = query.list();
            activeTaskBo.setActiveTaskCustomScheduleBo(activeTaskCustomScheduleBos);
          } else {
            searchQuery = "From ActiveTaskFrequencyBo ATBO where ATBO.activeTaskId=:activeTaskId";
            query =
                session.createQuery(searchQuery).setParameter("activeTaskId", activeTaskBo.getId());
            if (activeTaskBo
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {
              List<ActiveTaskFrequencyBo> activeTaskFrequencyBos = query.list();
              activeTaskBo.setActiveTaskFrequenciesList(activeTaskFrequencyBos);
            } else {
              ActiveTaskFrequencyBo activeTaskFrequencyBo =
                  (ActiveTaskFrequencyBo) query.uniqueResult();
              activeTaskBo.setActiveTaskFrequenciesBo(activeTaskFrequencyBo);
            }
          }
        }
        if (activeTaskBo.getVersion() != null) {
          activeTaskBo.setActiveTaskVersion(" (V" + activeTaskBo.getVersion() + ")");
        }
      }
    } catch (Exception e) {
      logger.error("StudyActiveTasksDAOImpl - getActiveTaskById() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getActiveTaskById() - Ends");
    return activeTaskBo;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ActivetaskFormulaBo> getActivetaskFormulas() {
    logger.entry("begin getActivetaskFormulas()");
    Session session = null;
    List<ActivetaskFormulaBo> activetaskFormulaList = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.createQuery("from ActivetaskFormulaBo");
      activetaskFormulaList = query.list();
    } catch (Exception e) {
      logger.error("StudyActiveTasksDAOImpl - getActivetaskFormulas() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getActivetaskFormulas() - Ends");
    return activetaskFormulaList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ActiveTaskMasterAttributeBo> getActiveTaskMasterAttributesByType(
      String activeTaskType) {
    logger.entry("begin getActiveTaskMasterAttributesByType()");
    Session session = null;
    List<ActiveTaskMasterAttributeBo> taskMasterAttributeBos = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session
              .createQuery(" from ActiveTaskMasterAttributeBo where taskTypeId=:activeTaskType")
              .setParameter("activeTaskType", Integer.parseInt(activeTaskType));
      taskMasterAttributeBos = query.list();
    } catch (Exception e) {
      logger.error("StudyActiveTasksDAOImpl - getActiveTaskMasterAttributesByType() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getActiveTaskMasterAttributesByType() - Ends");
    return taskMasterAttributeBos;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ActiveTaskListBo> getAllActiveTaskTypes(String platformType) {
    logger.entry("begin getAllActiveTaskTypes()");
    Session session = null;
    List<ActiveTaskListBo> activeTaskListBos = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();

      // to get only "Fetal kick counter" type of active task based on
      // Android platform
      Query query = null;
      if (StringUtils.isNotEmpty(platformType) && platformType.contains("A")) {
        query =
            session
                .createQuery(
                    "from ActiveTaskListBo a where a.taskName not in(:towerOfHanoi, :spatialSpanMemory)")
                .setParameter("towerOfHanoi", FdahpStudyDesignerConstants.TOWER_OF_HANOI)
                .setParameter("spatialSpanMemory", FdahpStudyDesignerConstants.SPATIAL_SPAN_MEMORY);
      } else {
        query = session.createQuery("from ActiveTaskListBo");
      }
      activeTaskListBos = query.list();
    } catch (Exception e) {
      logger.error("StudyActiveTasksDAOImpl - getAllActiveTaskTypes() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getAllActiveTaskTypes() - Ends");
    return activeTaskListBos;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<StatisticImageListBo> getStatisticImages() {
    logger.entry("begin getStatisticImages()");
    Session session = null;
    List<StatisticImageListBo> imageListBos = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.createQuery("from StatisticImageListBo");
      imageListBos = query.list();
    } catch (Exception e) {
      logger.error("StudyActiveTasksDAOImpl - getStatisticImages() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getStatisticImages() - Ends");
    return imageListBos;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ActiveTaskBo> getStudyActiveTasksByStudyId(String studyId, Boolean isLive) {
    logger.entry("begin getStudyActiveTasksByStudyId()");
    Session session = null;
    List<ActiveTaskBo> activeTasks = null;
    List<ActiveTaskListBo> activeTaskListBos = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(studyId)) {
        if (isLive) {
          String searchQuery =
              "SELECT ATB FROM ActiveTaskBo ATB where ATB.active IS NOT NULL and ATB.active=1 and ATB.customStudyId =:studyId"
                  + " and ATB.live=1 order by id";
          query = session.createQuery(searchQuery).setParameter("studyId", studyId);
        } else {
          query =
              session
                  .getNamedQuery("ActiveTaskBo.getActiveTasksByByStudyId")
                  .setInteger("studyId", Integer.parseInt(studyId));
        }

        activeTasks = query.list();

        query = session.createQuery("from ActiveTaskListBo");
        activeTaskListBos = query.list();

        if ((activeTasks != null)
            && !activeTasks.isEmpty()
            && (activeTaskListBos != null)
            && !activeTaskListBos.isEmpty()) {
          for (ActiveTaskBo activeTaskBo : activeTasks) {
            if (activeTaskBo.getTaskTypeId() != null) {
              for (ActiveTaskListBo activeTaskListBo : activeTaskListBos) {
                if (activeTaskListBo.getActiveTaskListId().intValue()
                    == activeTaskBo.getTaskTypeId().intValue()) {
                  activeTaskBo.setType(activeTaskListBo.getTaskName());
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyActiveTasksDAOImpl - getStudyActiveTasksByStudyId() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("getStudyActiveTasksByStudyId() - Ends");
    return activeTasks;
  }

  @Override
  public ActiveTaskBo saveOrUpdateActiveTask(ActiveTaskBo activeTaskBo, String customStudyId) {
    logger.entry("begin saveOrUpdateActiveTask()");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.saveOrUpdate(activeTaskBo);
      if (activeTaskBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.SCHEDULE)
          && (activeTaskBo != null)
          && (activeTaskBo.getId() != null)) {
        if ((activeTaskBo.getActiveTaskFrequenciesList() != null)
            && !activeTaskBo.getActiveTaskFrequenciesList().isEmpty()) {
          String deleteQuery =
              "delete from active_task_custom_frequencies where active_task_id=:activeTaskId";
          query =
              session
                  .createSQLQuery(deleteQuery)
                  .setParameter("activeTaskId", activeTaskBo.getId());
          query.executeUpdate();
          String deleteQuery2 =
              "delete from active_task_frequencies where active_task_id=:activeTaskId";
          query =
              session
                  .createSQLQuery(deleteQuery2)
                  .setParameter("activeTaskId", activeTaskBo.getId());
          query.executeUpdate();
          for (ActiveTaskFrequencyBo activeTaskFrequencyBo :
              activeTaskBo.getActiveTaskFrequenciesList()) {
            if (activeTaskFrequencyBo.getFrequencyTime() != null) {
              activeTaskFrequencyBo.setFrequencyTime(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTaskFrequencyBo.getFrequencyTime(),
                      FdahpStudyDesignerConstants.SDF_TIME,
                      FdahpStudyDesignerConstants.UI_SDF_TIME));
              if (activeTaskFrequencyBo.getActiveTaskId() == null) {
                activeTaskFrequencyBo.setId(null);
                activeTaskFrequencyBo.setActiveTaskId(activeTaskBo.getId());
              }
              session.saveOrUpdate(activeTaskFrequencyBo);
            }
          }
        }
        if (activeTaskBo.getActiveTaskFrequenciesList() != null) {
          ActiveTaskFrequencyBo activeTaskFrequencyBo = activeTaskBo.getActiveTaskFrequenciesBo();
          if ((activeTaskFrequencyBo.getFrequencyDate() != null)
              || (activeTaskFrequencyBo.getFrequencyTime() != null)
              || activeTaskBo
                  .getFrequency()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
            if (!activeTaskBo
                .getFrequency()
                .equalsIgnoreCase(activeTaskBo.getPreviousFrequency())) {
              String deleteQuery =
                  "delete from active_task_custom_frequencies where active_task_id=:activeTaskId";
              query =
                  session
                      .createSQLQuery(deleteQuery)
                      .setParameter("activeTaskId", activeTaskBo.getId());
              query.executeUpdate();
              String deleteQuery2 =
                  "delete from active_task_frequencies where active_task_id=:activeTaskId";
              query =
                  session
                      .createSQLQuery(deleteQuery2)
                      .setParameter("activeTaskId", activeTaskBo.getId());
              query.executeUpdate();
            }
            if (activeTaskFrequencyBo.getActiveTaskId() == null) {
              activeTaskFrequencyBo.setActiveTaskId(activeTaskBo.getId());
            }
            if ((activeTaskBo.getActiveTaskFrequenciesBo().getFrequencyDate() != null)
                && !activeTaskBo.getActiveTaskFrequenciesBo().getFrequencyDate().isEmpty()) {
              activeTaskFrequencyBo.setFrequencyDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTaskBo.getActiveTaskFrequenciesBo().getFrequencyDate(),
                      FdahpStudyDesignerConstants.UI_SDF_DATE,
                      FdahpStudyDesignerConstants.SD_DATE_FORMAT));
            }
            if ((activeTaskBo.getActiveTaskFrequenciesBo().getFrequencyTime() != null)
                && !activeTaskBo.getActiveTaskFrequenciesBo().getFrequencyTime().isEmpty()) {
              activeTaskBo
                  .getActiveTaskFrequenciesBo()
                  .setFrequencyTime(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          activeTaskBo.getActiveTaskFrequenciesBo().getFrequencyTime(),
                          FdahpStudyDesignerConstants.SDF_TIME,
                          FdahpStudyDesignerConstants.UI_SDF_TIME));
            }
            session.saveOrUpdate(activeTaskFrequencyBo);
          }
        }
        if ((activeTaskBo.getActiveTaskCustomScheduleBo() != null)
            && (activeTaskBo.getActiveTaskCustomScheduleBo().size() > 0)) {
          String deleteQuery =
              "delete from active_task_custom_frequencies where active_task_id=:activeTaskId";
          query =
              session
                  .createSQLQuery(deleteQuery)
                  .setParameter("activeTaskId", activeTaskBo.getId());
          query.executeUpdate();
          String deleteQuery2 =
              "delete from active_task_frequencies where active_task_id=:activeTaskId";
          query =
              session
                  .createSQLQuery(deleteQuery2)
                  .setParameter("activeTaskId", activeTaskBo.getId());
          query.executeUpdate();
          for (ActiveTaskCustomScheduleBo activeTaskCustomScheduleBo :
              activeTaskBo.getActiveTaskCustomScheduleBo()) {
            if (activeTaskCustomScheduleBo.getFrequencyStartTime() != null
                && activeTaskCustomScheduleBo.getFrequencyEndTime() != null) {
              if (activeTaskCustomScheduleBo.getActiveTaskId() == null) {
                activeTaskCustomScheduleBo.setActiveTaskId(activeTaskBo.getId());
              }
              if ((activeTaskCustomScheduleBo.getFrequencyStartDate() != null)
                  && !activeTaskCustomScheduleBo.getFrequencyStartDate().isEmpty()) {
                activeTaskCustomScheduleBo.setFrequencyStartDate(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        activeTaskCustomScheduleBo.getFrequencyStartDate(),
                        FdahpStudyDesignerConstants.UI_SDF_DATE,
                        FdahpStudyDesignerConstants.SD_DATE_FORMAT));
              }
              if ((activeTaskCustomScheduleBo.getFrequencyEndDate() != null)
                  && !activeTaskCustomScheduleBo.getFrequencyEndDate().isEmpty()) {
                activeTaskCustomScheduleBo.setFrequencyEndDate(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        activeTaskCustomScheduleBo.getFrequencyEndDate(),
                        FdahpStudyDesignerConstants.UI_SDF_DATE,
                        FdahpStudyDesignerConstants.SD_DATE_FORMAT));
              }
              if ((activeTaskCustomScheduleBo.getFrequencyStartTime() != null)
                  && !activeTaskCustomScheduleBo.getFrequencyStartTime().isEmpty()) {
                activeTaskCustomScheduleBo.setFrequencyStartTime(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        activeTaskCustomScheduleBo.getFrequencyStartTime(),
                        FdahpStudyDesignerConstants.SDF_TIME,
                        FdahpStudyDesignerConstants.UI_SDF_TIME));
              }
              if ((activeTaskCustomScheduleBo.getFrequencyEndTime() != null)
                  && !activeTaskCustomScheduleBo.getFrequencyEndTime().isEmpty()) {
                activeTaskCustomScheduleBo.setFrequencyEndTime(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        activeTaskCustomScheduleBo.getFrequencyEndTime(),
                        FdahpStudyDesignerConstants.SDF_TIME,
                        FdahpStudyDesignerConstants.UI_SDF_TIME));
              }
              activeTaskCustomScheduleBo.setxDaysSign(activeTaskCustomScheduleBo.isxDaysSign());
              if (activeTaskCustomScheduleBo.getTimePeriodFromDays() != null) {
                activeTaskCustomScheduleBo.setTimePeriodFromDays(
                    activeTaskCustomScheduleBo.getTimePeriodFromDays());
              }
              activeTaskCustomScheduleBo.setyDaysSign(activeTaskCustomScheduleBo.isyDaysSign());
              if (activeTaskCustomScheduleBo.getTimePeriodToDays() != null) {
                activeTaskCustomScheduleBo.setTimePeriodToDays(
                    activeTaskCustomScheduleBo.getTimePeriodToDays());
              }
              session.saveOrUpdate(activeTaskCustomScheduleBo);
            }
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyActiveTasksDAOImpl - saveOrUpdateActiveTask() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateActiveTask() - Ends");
    return activeTaskBo;
  }

  @Override
  public ActiveTaskBo saveOrUpdateActiveTaskInfo(
      ActiveTaskBo activeTaskBo, SessionObject sesObj, String customStudyId) {
    logger.entry("begin saveOrUpdateActiveTaskInfo()");
    Session session = null;
    StudySequenceBo studySequence = null;
    List<ActiveTaskAtrributeValuesBo> taskAttributeValueBos = new ArrayList<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if ((activeTaskBo.getTaskAttributeValueBos() != null)
          && !activeTaskBo.getTaskAttributeValueBos().isEmpty()) {
        taskAttributeValueBos = activeTaskBo.getTaskAttributeValueBos();
      }
      session.saveOrUpdate(activeTaskBo);
      if ((taskAttributeValueBos != null) && !taskAttributeValueBos.isEmpty()) {
        for (ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo : taskAttributeValueBos) {
          if (activeTaskAtrributeValuesBo.isAddToDashboard()) {
            if (!activeTaskAtrributeValuesBo.isAddToLineChart()) {
              activeTaskAtrributeValuesBo.setTimeRangeChart(null);
              activeTaskAtrributeValuesBo.setRollbackChat(null);
              activeTaskAtrributeValuesBo.setTitleChat(null);
            }
            if (!activeTaskAtrributeValuesBo.isUseForStatistic()) {
              activeTaskAtrributeValuesBo.setIdentifierNameStat(null);
              activeTaskAtrributeValuesBo.setDisplayNameStat(null);
              activeTaskAtrributeValuesBo.setDisplayUnitStat(null);
              activeTaskAtrributeValuesBo.setUploadTypeStat(null);
              activeTaskAtrributeValuesBo.setFormulaAppliedStat(null);
              activeTaskAtrributeValuesBo.setTimeRangeStat(null);
            }
            activeTaskAtrributeValuesBo.setActiveTaskId(activeTaskBo.getId());
            activeTaskAtrributeValuesBo.setActive(1);
            if (activeTaskAtrributeValuesBo.getAttributeValueId() == null) {
              session.save(activeTaskAtrributeValuesBo);
            } else {
              session.update(activeTaskAtrributeValuesBo);
            }
          }
        }
      }

      if (StringUtils.isNotEmpty(activeTaskBo.getButtonText())) {
        studySequence =
            (StudySequenceBo)
                session
                    .getNamedQuery("getStudySequenceByStudyId")
                    .setInteger("studyId", activeTaskBo.getStudyId())
                    .uniqueResult();
        if (studySequence != null) {
          studySequence.setStudyExcActiveTask(false);
        }
        session.saveOrUpdate(studySequence);
      }

      if (!activeTaskBo
          .getButtonText()
          .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {

        auditLogDAO.updateDraftToEditedStatus(
            session,
            transaction,
            sesObj.getUserId(),
            FdahpStudyDesignerConstants.DRAFT_ACTIVETASK,
            activeTaskBo.getStudyId());
        // Notification Purpose needed Started
        queryString = " From StudyBo where customStudyId=:customStudyId and live=1";
        StudyBo studyBo =
            (StudyBo)
                session
                    .createQuery(queryString)
                    .setParameter("customStudyId", customStudyId)
                    .uniqueResult();
        if (studyBo != null) {
          queryString = " From StudyBo where id=:studyId";
          StudyBo draftStudyBo =
              (StudyBo)
                  session
                      .createQuery(queryString)
                      .setParameter("studyId", activeTaskBo.getStudyId())
                      .uniqueResult();
          NotificationBO notificationBO = null;
          queryString = "From NotificationBO where activeTaskId=:activeTaskId";
          notificationBO =
              (NotificationBO)
                  session
                      .createQuery(queryString)
                      .setParameter("activeTaskId", activeTaskBo.getId())
                      .uniqueResult();
          if (notificationBO == null) {
            notificationBO = new NotificationBO();
            notificationBO.setStudyId(activeTaskBo.getStudyId());
            notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
            if (StringUtils.isNotEmpty(studyBo.getAppId())) {
              notificationBO.setAppId(studyBo.getAppId());
            }
            notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
            notificationBO.setNotificationSubType(
                FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ACTIVITY);
            notificationBO.setNotificationScheduleType(
                FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
            notificationBO.setActiveTaskId(activeTaskBo.getId());
            notificationBO.setNotificationStatus(false);
            notificationBO.setCreatedBy(sesObj.getUserId());
            notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            notificationBO.setNotificationSent(false);
          } else {
            notificationBO.setModifiedBy(sesObj.getUserId());
            notificationBO.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          }
          notificationBO.setNotificationText(
              FdahpStudyDesignerConstants.NOTIFICATION_ACTIVETASK_TEXT
                  .replace("$shortTitle", activeTaskBo.getDisplayName())
                  .replace("$customId", draftStudyBo.getName()));
          if (!notificationBO.isNotificationSent()) {
            session.saveOrUpdate(notificationBO);
          }
        }
        // Notification Purpose needed End
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyActiveTasksDAOImpl - saveOrUpdateActiveTaskInfo() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateActiveTaskInfo() - Ends");
    return activeTaskBo;
  }

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public boolean validateActiveTaskAttrById(
      Integer studyId,
      String activeTaskAttName,
      String activeTaskAttIdVal,
      String activeTaskAttIdName,
      String customStudyId) {
    logger.entry("begin validateActiveTaskAttrById()");
    boolean flag = false;
    Session session = null;
    String queryString = "";
    String subString = "";
    List<ActiveTaskBo> taskBos = null;
    new ArrayList<>();
    List<QuestionnaireBo> questionnaireBo = null;
    List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos = null;
    List<QuestionsBo> questionnairesStepsBo = null;
    List<String> idArr = new ArrayList<String>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if ((studyId != null)
          && StringUtils.isNotEmpty(activeTaskAttName)
          && StringUtils.isNotEmpty(activeTaskAttIdVal)) {

        // to check uniqueness of chart short title in activity(active
        // task and questionnaire) of study
        if (activeTaskAttName.equalsIgnoreCase(FdahpStudyDesignerConstants.SHORT_NAME_STATISTIC)) {
          if ((customStudyId != null) && !customStudyId.isEmpty()) {
            if (!activeTaskAttIdName.equals("static")) {
              if (activeTaskAttIdName.contains(",")) {
                String[] arr;
                arr = activeTaskAttIdName.split(",");
                if ((arr != null) && (arr.length > 0)) {
                  for (String id : arr) {
                    if (!id.isEmpty()) {
                      idArr.add(id);
                    }
                  }
                }
                activeTaskAttIdName = StringUtils.join(idArr, ',');
              }
              subString = " and attributeValueId NOT IN(:activeTaskAttIdName)";
            }
            // to check chart short title exist in active task or
            // not
            queryString =
                "from ActiveTaskAtrributeValuesBo where activeTaskId in(select id from ActiveTaskBo where studyId IN "
                    + "(select id From StudyBo SBO WHERE customStudyId=:customStudyId )) and identifierNameStat=:activeTaskAttIdVal "
                    + subString;

            if (!activeTaskAttIdName.equals("static")) {
              activeTaskAtrributeValuesBos =
                  session
                      .createQuery(queryString)
                      .setParameter("customStudyId", customStudyId)
                      .setParameter("activeTaskAttIdVal", activeTaskAttIdVal)
                      .setParameterList("activeTaskAttIdName", idArr)
                      .list();
            } else {
              activeTaskAtrributeValuesBos =
                  session
                      .createQuery(queryString)
                      .setParameter("customStudyId", customStudyId)
                      .setParameter("activeTaskAttIdVal", activeTaskAttIdVal)
                      .list();
            }

            if ((activeTaskAtrributeValuesBos != null) && !activeTaskAtrributeValuesBos.isEmpty()) {
              flag = true;
            } else {
              // to check chart short title exist in question of
              // questionnaire
              queryString =
                  "From QuestionsBo QBO where QBO.id IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId IN (select id from QuestionnaireBo Q where Q.studyId in(select id From StudyBo SBO WHERE customStudyId= :customStudyId"
                      + ")) and QSBO.stepType=:type) and QBO.statShortName=:activeTaskAttIdVal";

              query =
                  session
                      .createQuery(queryString)
                      .setParameter("type", FdahpStudyDesignerConstants.QUESTION_STEP)
                      .setParameter("customStudyId", customStudyId)
                      .setParameter("activeTaskAttIdVal", activeTaskAttIdVal);
              questionnairesStepsBo = query.list();
              if ((questionnairesStepsBo != null) && !questionnairesStepsBo.isEmpty()) {
                flag = true;
              } else {
                // to check chart short title exist in form
                // question of questionnaire
                queryString =
                    "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
                        + " and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.study_id IN(select id From studies SBO WHERE custom_study_id=:customStudyId"
                        + ") and QSBO.step_type='Form' and QBO.stat_short_name=:activeTaskAttIdVal";
                BigInteger subCount =
                    (BigInteger)
                        session
                            .createSQLQuery(queryString)
                            .setParameter("customStudyId", customStudyId)
                            .setParameter("activeTaskAttIdVal", activeTaskAttIdVal)
                            .uniqueResult();
                if ((subCount != null) && (subCount.intValue() > 0)) {
                  flag = true;
                } else {
                  flag = false;
                }
              }
            }
          } else {
            if (!activeTaskAttIdName.equals("static")) {
              if (activeTaskAttIdName.contains(",")) {
                String[] arr;
                arr = activeTaskAttIdName.split(",");
                if ((arr != null) && (arr.length > 0)) {
                  for (String id : arr) {
                    if (!id.isEmpty()) {
                      idArr.add(id);
                    }
                  }
                }
              }
              subString = " and attributeValueId NOT IN(:activeTaskAttIdName)";
            }
            // to check chart short title exist in active task or
            // not
            queryString =
                "from ActiveTaskAtrributeValuesBo where activeTaskId in(select id from ActiveTaskBo where studyId=:studyId)"
                    + " and identifierNameStat=:activeTaskAttIdVal"
                    + subString;

            if (!activeTaskAttIdName.equals("static")) {
              activeTaskAtrributeValuesBos =
                  session
                      .createQuery(queryString)
                      .setParameterList("activeTaskAttIdName", Arrays.asList(activeTaskAttIdName))
                      .setParameter("studyId", studyId)
                      .setParameter("activeTaskAttIdVal", activeTaskAttIdVal)
                      .list();
            } else {
              activeTaskAtrributeValuesBos =
                  session
                      .createQuery(queryString)
                      .setParameter("studyId", studyId)
                      .setParameter("activeTaskAttIdVal", activeTaskAttIdVal)
                      .list();
            }

            if ((activeTaskAtrributeValuesBos != null) && !activeTaskAtrributeValuesBos.isEmpty()) {
              flag = true;
            } else {
              // to check chart short title exist in question of
              // questionnaire
              queryString =
                  "From QuestionsBo QBO where QBO.id IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId IN (select id from QuestionnaireBo Q where Q.studyId=:studyId"
                      + ") and QSBO.stepType=:type) and QBO.statShortName=:activeTaskAttIdVal";
              query =
                  session
                      .createQuery(queryString)
                      .setParameter("type", FdahpStudyDesignerConstants.QUESTION_STEP)
                      .setParameter("studyId", studyId)
                      .setParameter("activeTaskAttIdVal", activeTaskAttIdVal);
              questionnairesStepsBo = query.list();
              if ((questionnairesStepsBo != null) && !questionnairesStepsBo.isEmpty()) {
                flag = true;
              } else {
                // to check chart short title exist in form
                // question of questionnaire
                queryString =
                    "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
                        + "and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.study_id=:studyId"
                        + " and QSBO.step_type='Form' and QBO.stat_short_name=:activeTaskAttIdVal";
                BigInteger subCount =
                    (BigInteger)
                        session
                            .createSQLQuery(queryString)
                            .setParameter("studyId", studyId)
                            .setParameter("activeTaskAttIdVal", activeTaskAttIdVal)
                            .uniqueResult();
                if ((subCount != null) && (subCount.intValue() > 0)) {
                  flag = true;
                } else {
                  flag = false;
                }
              }
            }
          }
        } else if (activeTaskAttName.equalsIgnoreCase(FdahpStudyDesignerConstants.SHORT_TITLE)) {
          // to check uniqueness of short title in activity(active
          // task and questionnaire) of study
          if ((customStudyId != null) && !customStudyId.isEmpty()) {
            // to check short title exist in active task or not
            queryString =
                "from ActiveTaskBo where studyId IN (select id From StudyBo SBO WHERE customStudyId=:customStudyId ) and shortTitle=:activeTaskAttIdVal";
            taskBos =
                session
                    .createQuery(queryString)
                    .setParameter("customStudyId", customStudyId)
                    .setParameter("activeTaskAttIdVal", activeTaskAttIdVal)
                    .list();
            if ((taskBos != null) && !taskBos.isEmpty()) {
              flag = true;
            } else {
              // to check short title exist in questionnaire or
              // not
              queryString =
                  "From QuestionnaireBo QBO where QBO.studyId IN(select id From StudyBo SBO WHERE customStudyId=:customStudyId) and QBO.shortTitle=:activeTaskAttIdVal";
              query =
                  session
                      .createQuery(queryString)
                      .setParameter("customStudyId", customStudyId)
                      .setParameter("activeTaskAttIdVal", activeTaskAttIdVal);
              questionnaireBo = query.list();
              if ((questionnaireBo != null) && !questionnaireBo.isEmpty()) {
                flag = true;
              } else {
                flag = false;
              }
            }
          } else {
            // to check short title exist in active task or not
            queryString =
                "from ActiveTaskBo where studyId=:studyId and shortTitle=:activeTaskAttIdVal ";
            taskBos =
                session
                    .createQuery(queryString)
                    .setParameter("studyId", studyId)
                    .setParameter("activeTaskAttIdVal", activeTaskAttIdVal)
                    .list();
            if ((taskBos != null) && !taskBos.isEmpty()) {
              flag = true;
            } else {
              // to check short title exist in questionnaire or
              // not
              questionnaireBo =
                  session
                      .getNamedQuery("checkQuestionnaireShortTitle")
                      .setInteger("studyId", studyId)
                      .setString("shortTitle", activeTaskAttIdVal)
                      .list();
              if ((questionnaireBo != null) && !questionnaireBo.isEmpty()) {
                flag = true;
              } else {
                flag = false;
              }
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyActiveTasksDAOImpl - validateActiveTaskAttrById() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("validateActiveTaskAttrById() - Ends");
    return flag;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ActiveStatisticsBean> validateActiveTaskStatIds(
      String customStudyId, List<ActiveStatisticsBean> activeStatisticsBeans) {
    logger.entry("begin validateActiveTaskStatIds()");
    Session session = null;
    List<String> ids = new ArrayList<>();
    String subString = "";
    String queryString = "";
    List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if ((activeStatisticsBeans != null)
          && !activeStatisticsBeans.isEmpty()
          && StringUtils.isNotEmpty(customStudyId)) {
        if (!activeStatisticsBeans.get(0).getId().contains("static")) {
          for (ActiveStatisticsBean activeStatisticsBean : activeStatisticsBeans) {
            if (StringUtils.isNotEmpty(activeStatisticsBean.getIdName())) {
              ids.add(activeStatisticsBean.getIdName());
            }
          }
          if (!ids.isEmpty()) {
            subString = " AND attributeValueId NOT IN(:ids)";
          }
        }
        // checking each statistics data validate and get which one have
        // duplicate value
        for (ActiveStatisticsBean activeStatisticsBean : activeStatisticsBeans) {
          if (!activeStatisticsBean.getDbVal().equalsIgnoreCase(activeStatisticsBean.getIdVal())) {
            queryString =
                "from ActiveTaskAtrributeValuesBo where activeTaskId in(select id from ActiveTaskBo where studyId IN "
                    + "(select id From StudyBo SBO WHERE customStudyId=:customStudyId"
                    + ")) and identifierNameStat =:identifierNameStat"
                    + subString;

            if (!activeStatisticsBeans.get(0).getId().contains("static")) {
              activeTaskAtrributeValuesBos =
                  session
                      .createQuery(queryString)
                      .setParameterList("ids", ids)
                      .setParameter("customStudyId", customStudyId)
                      .setParameter("identifierNameStat", activeStatisticsBean.getIdVal())
                      .list();
            } else {
              activeTaskAtrributeValuesBos =
                  session
                      .createQuery(queryString)
                      .setParameter("customStudyId", customStudyId)
                      .setParameter("identifierNameStat", activeStatisticsBean.getIdVal())
                      .list();
            }

            if ((activeTaskAtrributeValuesBos != null) && !activeTaskAtrributeValuesBos.isEmpty()) {
              activeStatisticsBean.setType(true);
              break;
            } else {
              activeStatisticsBean.setType(false);
            }
          } else {
            activeStatisticsBean.setType(false);
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyActiveTasksDAOImpl - validateActiveTaskStatIds() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("validateActiveTaskStatIds() - Ends");
    return activeStatisticsBeans;
  }
}
