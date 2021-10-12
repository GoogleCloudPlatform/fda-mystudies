/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.IMPORT_FAILED_DUE_TO_ALREADY_USED_URL;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.IMPORT_FAILED_DUE_TO_ANOMOLIES_DETECTED_IN_FILLE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.IMPORT_FAILED_DUE_TO_INCOMPATIBLE_VERSION;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.INVALID_URL;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.NOTIFICATION_NOTIMMEDIATE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.PUBLISHED_VERSION;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.SUCCESS;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.WORKING_VERSION;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.YES;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.ActiveTaskAtrributeValuesBo;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.ComprehensionTestQuestionBo;
import com.fdahpstudydesigner.bo.ComprehensionTestResponseBo;
import com.fdahpstudydesigner.bo.ConsentBo;
import com.fdahpstudydesigner.bo.ConsentInfoBo;
import com.fdahpstudydesigner.bo.EligibilityBo;
import com.fdahpstudydesigner.bo.EligibilityTestBo;
import com.fdahpstudydesigner.bo.FormBo;
import com.fdahpstudydesigner.bo.FormMappingBo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.QuestionReponseTypeBo;
import com.fdahpstudydesigner.bo.QuestionResponseSubTypeBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnaireCustomScheduleBo;
import com.fdahpstudydesigner.bo.QuestionnairesFrequenciesBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.ResourceBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudyPageBo;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import com.fdahpstudydesigner.dao.NotificationDAO;
import com.fdahpstudydesigner.dao.StudyActiveTasksDAO;
import com.fdahpstudydesigner.dao.StudyDAO;
import com.fdahpstudydesigner.dao.StudyDAOImpl;
import com.fdahpstudydesigner.dao.StudyQuestionnaireDAO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.IdGenerator;
import com.fdahpstudydesigner.util.ServletContextHolder;
import com.fdahpstudydesigner.util.SessionObject;
import com.fdahpstudydesigner.util.StudyExportSqlQueries;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StudyExportImportService {

  private static final String PRE_LAUNCH = "Pre-launch";

  private static final String STUDY_ID = "STUDY_ID_";

  private static final String CUSTOM_STUDY_ID = "CUSTOM_STUDY_ID_";

  private static final String COMPREHENSION_TEST_QUESTION_ID = "COMPREHENSION_TEST_QUESTION_ID_";

  private static final String QUESTIONNAIRES_ID = "QUESTIONNAIRES_ID_";

  private static final String INSTRUCTION_FORM_ID = "INSTRUCTION_FORM_ID_";

  private static final String ACTIVETASK_ID = "ACTIVETASK_ID_";

  private static final String NEW_ELIGIBILITY_ID = "NEW_ELIGIBILITY_ID_";

  private static final String ANCHORDATE_ID = "ANCHORDATE_ID_";

  private static final String IMPORTED = "Imported ";

  private static XLogger logger =
      XLoggerFactory.getXLogger(StudyExportImportService.class.getName());

  @Autowired private StudyDAO studyDao;

  @Autowired private StudyQuestionnaireDAO studyQuestionnaireDAO;

  @Autowired private NotificationDAO notificationDAO;

  @Autowired private StudyActiveTasksDAO studyActiveTasksDAO;

  @Autowired StudyDAOImpl study;

  private JdbcTemplate jdbcTemplate;

  HibernateTemplate hibernateTemplate;

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @Autowired
  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private static final String PATH_SEPARATOR = "/";

  private static final String UNDER_DIRECTORY = "export-studies";

  public String exportStudy(
      String studyId, String copyVersion, String userId, AuditLogEventRequest auditRequest) {

    // This map contains new primary key and foreign key values
    final Map<String, String> customIdsMap = new HashMap<>();

    StudyBo studyBo = studyDao.getStudy(studyId);

    if (studyBo != null) {
      auditRequest.setStudyId(studyBo.getCustomStudyId());
      auditRequest.setStudyVersion(studyBo.getVersion().toString());
      auditRequest.setAppId(studyBo.getAppId());
      customIdsMap.put(STUDY_ID + studyBo.getId(), IdGenerator.id());
      customIdsMap.put(CUSTOM_STUDY_ID + studyBo.getCustomStudyId(), null);

      StudySequenceBo studySequenceBo = studyDao.getStudySequenceByStudyId(studyBo.getId());

      List<AnchorDateTypeBo> anchorDateList =
          studyDao.getAnchorDateDetails(studyBo.getId(), studyBo.getCustomStudyId());
      if (CollectionUtils.isNotEmpty(anchorDateList)) {
        for (AnchorDateTypeBo anchorDate : anchorDateList) {
          customIdsMap.put(ANCHORDATE_ID + anchorDate.getId(), IdGenerator.id());
        }
      }

      List<StudyPageBo> studypageList = studyDao.getOverviewStudyPagesById(studyBo.getId(), userId);

      EligibilityBo eligibilityBo = studyDao.getStudyEligibiltyByStudyId(studyBo.getId());

      List<EligibilityTestBo> eligibilityBoList = new ArrayList<>();
      if (eligibilityBo != null) {

        eligibilityBoList =
            studyDao.viewEligibilityTestQusAnsByEligibilityId(eligibilityBo.getId());
        customIdsMap.put(NEW_ELIGIBILITY_ID + eligibilityBo.getId(), IdGenerator.id());
      }

      List<ConsentBo> consentBoList =
          studyDao.getConsentListForStudy(studyBo.getId(), studyBo.getCustomStudyId(), copyVersion);

      List<ConsentInfoBo> consentInfoBoList =
          studyDao.getConsentInfoList(studyBo.getId(), studyBo.getCustomStudyId(), copyVersion);

      List<NotificationBO> notificationBOs =
          notificationDAO.getNotificationsList(
              studyBo.getId(), studyBo.getCustomStudyId(), copyVersion);

      List<ResourceBO> resourceBOs = studyDao.getResourceList(studyBo.getId());

      // This list contains INSERT SQL statements with original study content as values
      List<String> insertSqlStatements = new ArrayList<>();

      try {

        // prepare INSERT SQL statements
        addStudiesInsertSql(studyBo, insertSqlStatements, customIdsMap);
        addStudySequenceInsertSql(studySequenceBo, insertSqlStatements, customIdsMap);

        addAnchorDateInsertSql(anchorDateList, insertSqlStatements, customIdsMap, studyBo.getId());
        addStudypagesListInsertSql(studypageList, insertSqlStatements, customIdsMap);

        addEligibilityInsertSql(eligibilityBo, insertSqlStatements, customIdsMap);
        addEligibilityTestListInsertSql(eligibilityBoList, insertSqlStatements, customIdsMap);

        addConsentBoListInsertSql(
            consentBoList, insertSqlStatements, customIdsMap, studyBo.getId());
        addConsentInfoBoListInsertSql(
            consentInfoBoList, insertSqlStatements, customIdsMap, studyBo.getId());

        prepareInsertSqlQueriesForComprehensionTest(customIdsMap, insertSqlStatements, studyBo);

        prepareInsertSqlQueriesForQuestionnaires(
            customIdsMap, insertSqlStatements, studyBo, copyVersion);

        prepareInsertSqlQueriesForStudyActiveTasks(
            customIdsMap, insertSqlStatements, studyBo, copyVersion);

        addNotificationInsertSql(
            notificationBOs, insertSqlStatements, customIdsMap, copyVersion, studyBo);

        addResourceInsertSql(resourceBOs, insertSqlStatements, customIdsMap);

        // This method export study to google cloud storage
        return saveFileToCloudStorage(studyBo, insertSqlStatements);

      } catch (Exception e) {
        logger.error(String.format("export study failed due to %s", e.getMessage()), e);
        return FdahpStudyDesignerConstants.EXPORT_FAILURE_MSG;
      }
    }
    return FdahpStudyDesignerConstants.EXPORT_FAILURE_MSG;
  }

  private void prepareInsertSqlQueriesForStudyActiveTasks(
      final Map<String, String> customIdsMap,
      List<String> insertSqlStatements,
      StudyBo studyBo,
      String copyVersion)
      throws Exception {

    List<ActiveTaskBo> activeTaskBos =
        studyActiveTasksDAO.getStudyActiveTaskByStudyId(
            studyBo.getId(), studyBo.getCustomStudyId(), copyVersion);

    Map<String, List<ActiveTaskCustomScheduleBo>> activeTaskCustomFrequencyMap = new HashMap<>();
    Map<String, List<ActiveTaskFrequencyBo>> activeTaskFrequencyMap = new HashMap<>();
    List<String> activeTaskIds = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(activeTaskBos)) {
      for (ActiveTaskBo activeTaskBo : activeTaskBos) {
        activeTaskIds.add(activeTaskBo.getId());
        customIdsMap.put(ACTIVETASK_ID + activeTaskBo.getId(), IdGenerator.id());
        if (activeTaskBo
            .getFrequency()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
          List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleBoList =
              studyActiveTasksDAO.getActivetaskCustomFrequencies(activeTaskBo.getId());
          activeTaskCustomFrequencyMap.put(activeTaskBo.getId(), activeTaskCustomScheduleBoList);
        } else {
          List<ActiveTaskFrequencyBo> activeTaskFrequencyBoList =
              studyActiveTasksDAO.getActiveTaskFrequency(activeTaskBo.getId());
          activeTaskFrequencyMap.put(activeTaskBo.getId(), activeTaskFrequencyBoList);
        }
      }
    }

    List<ActiveTaskCustomScheduleBo> activeTaskcustomFrequencyList = new ArrayList<>();
    for (Map.Entry<String, List<ActiveTaskCustomScheduleBo>> entry :
        activeTaskCustomFrequencyMap.entrySet()) {
      Integer sequenceNumber = 0;
      for (ActiveTaskCustomScheduleBo activeTaskCustomScheduleBo : entry.getValue()) {
        activeTaskCustomScheduleBo.setSequenceNumber(sequenceNumber++);
        activeTaskcustomFrequencyList.add(activeTaskCustomScheduleBo);
      }
    }

    List<ActiveTaskFrequencyBo> activeTaskFrequencyList = new ArrayList<>();
    for (Map.Entry<String, List<ActiveTaskFrequencyBo>> entry : activeTaskFrequencyMap.entrySet()) {
      Integer sequenceNumber = 0;
      for (ActiveTaskFrequencyBo activeTaskFrequencyScheduleBo : entry.getValue()) {
        activeTaskFrequencyScheduleBo.setSequenceNumber(sequenceNumber++);
        activeTaskFrequencyList.add(activeTaskFrequencyScheduleBo);
      }
    }

    List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos =
        studyActiveTasksDAO.getActiveTaskAtrributeValuesByActiveTaskId(activeTaskIds);

    addStudyActiveTaskInsertSql(activeTaskBos, insertSqlStatements, customIdsMap, studyBo.getId());

    addActiveTaskAtrributeValuesInsertSql(
        activeTaskAtrributeValuesBos, insertSqlStatements, customIdsMap);

    addActiveTaskCustomScheduleBoInsertSqlQuery(
        activeTaskcustomFrequencyList, insertSqlStatements, customIdsMap);

    addActiveTaskFrequencyBoInsertSqlQuery(
        activeTaskFrequencyList, insertSqlStatements, customIdsMap);
  }

  private void prepareInsertSqlQueriesForQuestionnaires(
      final Map<String, String> customIdsMap,
      List<String> insertSqlStatements,
      StudyBo studyBo,
      String copyVersion)
      throws Exception {

    List<QuestionnaireBo> questionnairesList =
        studyQuestionnaireDAO.getStudyQuestionnairesByStudyId(
            studyBo.getId(), studyBo.getCustomStudyId(), copyVersion);

    List<String> questionnaireIds = new ArrayList<>();
    Map<String, List<QuestionnaireCustomScheduleBo>> customScheduleMap = new HashMap<>();
    Map<String, List<QuestionnairesFrequenciesBo>> frequencyMap = new HashMap<>();

    if (CollectionUtils.isNotEmpty(questionnairesList)) {
      Integer sequenceNumber = 0;
      for (QuestionnaireBo questionnaireBo : questionnairesList) {
        questionnaireIds.add(questionnaireBo.getId());
        customIdsMap.put(QUESTIONNAIRES_ID + questionnaireBo.getId(), IdGenerator.id());
        questionnaireBo.setSequenceNumber(sequenceNumber++);
        if (questionnaireBo
            .getFrequency()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
          List<QuestionnaireCustomScheduleBo> list =
              studyQuestionnaireDAO.getQuestionnaireCustomSchedules(questionnaireBo.getId());
          customScheduleMap.put(questionnaireBo.getId(), list);
        } else {
          List<QuestionnairesFrequenciesBo> frequencyList =
              studyQuestionnaireDAO.getQuestionnairesFrequencies(questionnaireBo.getId());
          frequencyMap.put(questionnaireBo.getId(), frequencyList);
        }
      }
    }

    List<QuestionnaireCustomScheduleBo> customList = new ArrayList<>();
    for (Map.Entry<String, List<QuestionnaireCustomScheduleBo>> entry :
        customScheduleMap.entrySet()) {
      Integer sequenceNumber = 0;

      for (QuestionnaireCustomScheduleBo questionnaireCustomScheduleBo : entry.getValue()) {
        questionnaireCustomScheduleBo.setSequenceNumber(sequenceNumber++);
        customList.add(questionnaireCustomScheduleBo);
      }
    }

    List<QuestionnairesFrequenciesBo> frequencyList = new ArrayList<>();
    for (Map.Entry<String, List<QuestionnairesFrequenciesBo>> entry : frequencyMap.entrySet()) {

      Integer sequenceNumber = 0;
      for (QuestionnairesFrequenciesBo questionnairesFrequenciesBo : entry.getValue()) {
        questionnairesFrequenciesBo.setSequenceNumber(sequenceNumber++);
        frequencyList.add(questionnairesFrequenciesBo);
      }
    }

    List<QuestionnairesStepsBo> questionnairesStepsList =
        studyQuestionnaireDAO.getQuestionnairesStepsList(questionnaireIds);

    List<String> instructionFormIds = new ArrayList<>();
    Map<String, String> questionMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(questionnairesStepsList)) {
      for (QuestionnairesStepsBo questionnairesStepsBo : questionnairesStepsList) {
        instructionFormIds.add(questionnairesStepsBo.getInstructionFormId());
        questionMap.put(questionnairesStepsBo.getStepId(), IdGenerator.id());
      }
    }

    List<FormBo> formsList = studyQuestionnaireDAO.getFormsByInstructionFormIds(instructionFormIds);

    List<String> formQuestionIds = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(formsList)) {
      for (FormBo formBo : formsList) {
        formQuestionIds.add(formBo.getFormId());
      }
    }

    List<String> questionIds = studyQuestionnaireDAO.getQuestionsByFormIds(formQuestionIds);
    instructionFormIds.addAll(questionIds);

    List<QuestionsBo> questionsList =
        studyQuestionnaireDAO.getQuestionsByInstructionFormIds(instructionFormIds);
    Map<String, List<QuestionResponseSubTypeBo>> responseSubTypeMap = new HashMap<>();

    for (QuestionsBo questionsBo : questionsList) {
      List<QuestionResponseSubTypeBo> list =
          studyQuestionnaireDAO.getQuestionResponseSubTypes(questionsBo.getId());
      responseSubTypeMap.put(questionsBo.getId(), list);
    }

    List<QuestionResponseSubTypeBo> responseList = new ArrayList<>();
    for (Map.Entry<String, List<QuestionResponseSubTypeBo>> entry : responseSubTypeMap.entrySet()) {
      Integer sequenceNumber = 0;
      for (QuestionResponseSubTypeBo questionResponseSubTypeBo : entry.getValue()) {
        questionResponseSubTypeBo.setSequenceNumber(sequenceNumber++);
        responseList.add(questionResponseSubTypeBo);
      }
    }

    List<FormMappingBo> formMappingList =
        studyQuestionnaireDAO.getFormMappingbyInstructionFormIds(instructionFormIds);

    List<InstructionsBo> instructionList =
        studyQuestionnaireDAO.getInstructionListByInstructionFormIds(instructionFormIds);

    List<QuestionResponseSubTypeBo> questionResponseSubTypeBoList =
        studyQuestionnaireDAO.getQuestionResponseSubTypeBoByInstructionFormIds(instructionFormIds);

    List<QuestionReponseTypeBo> questionResponseTypeBo =
        studyQuestionnaireDAO.getQuestionResponseTypeBoByInstructionFormIds(instructionFormIds);

    getNewInstructionFormIds(
        questionsList,
        formMappingList,
        instructionList,
        formsList,
        questionResponseSubTypeBoList,
        questionResponseTypeBo,
        customIdsMap);

    addQuestionnaireBoListInsertSql(questionnairesList, insertSqlStatements, customIdsMap, studyBo);

    addQuestionnaireFrequenciesBoInsertSql(frequencyList, insertSqlStatements, customIdsMap);

    addQuestionnaireCustomScheduleBoInsertSql(customList, insertSqlStatements, customIdsMap);

    addQuestionListInsertSql(questionsList, insertSqlStatements, customIdsMap);

    addFormMappingListInsertSql(formMappingList, insertSqlStatements, customIdsMap);

    addFormsListInsertSql(formsList, insertSqlStatements, customIdsMap);

    addInstructionInsertSql(instructionList, insertSqlStatements, customIdsMap);

    addQuestionsResponseSubTypeInsertSql(
        responseList, insertSqlStatements, customIdsMap, questionMap);

    addQuestionsResponseTypeInsertSql(questionResponseTypeBo, insertSqlStatements, customIdsMap);

    addQuestionnairesStepsListInsertSql(
        questionnairesStepsList, insertSqlStatements, customIdsMap, questionMap);
  }

  private void prepareInsertSqlQueriesForComprehensionTest(
      final Map<String, String> customIdsMap, List<String> insertSqlStatements, StudyBo studyBo)
      throws Exception {

    List<ComprehensionTestQuestionBo> comprehensionTestQuestionBoList =
        studyDao.getComprehensionTestQuestionList(studyBo.getId());

    Map<String, List<ComprehensionTestResponseBo>> comprehensionTestResponseMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(comprehensionTestQuestionBoList)) {
      for (ComprehensionTestQuestionBo comprehensionTestQuestionBo :
          comprehensionTestQuestionBoList) {
        customIdsMap.put(
            COMPREHENSION_TEST_QUESTION_ID + comprehensionTestQuestionBo.getId(), IdGenerator.id());
        // get responses for each question
        List<ComprehensionTestResponseBo> comprehensionTestResponseBoList =
            studyDao.getComprehensionTestResponses(comprehensionTestQuestionBo.getId());
        comprehensionTestResponseMap.put(
            comprehensionTestQuestionBo.getId(), comprehensionTestResponseBoList);
      }
    }

    List<ComprehensionTestResponseBo> comprehensionTestResponses = new ArrayList<>();
    for (Map.Entry<String, List<ComprehensionTestResponseBo>> entry :
        comprehensionTestResponseMap.entrySet()) {
      Integer responseSequence = 0;
      for (ComprehensionTestResponseBo comprehensionTestResponse : entry.getValue()) {
        comprehensionTestResponse.setSequenceNumber(responseSequence++);
        comprehensionTestResponses.add(comprehensionTestResponse);
      }
    }

    addComprehensionTestQuestionListInsertSql(
        comprehensionTestQuestionBoList, insertSqlStatements, customIdsMap);

    addComprehensionTestResponseBoListInsertSql(
        comprehensionTestResponses, insertSqlStatements, customIdsMap);
  }

  private void addFormsListInsertSql(
      List<FormBo> formsList, List<String> insertSqlStatements, Map<String, String> customIdsMap)
      throws Exception {
    List<String> formBoInsertQueryList = new ArrayList<>();
    if (CollectionUtils.isEmpty(formsList)) {
      return;
    }

    String formInsertQuery = null;
    for (FormBo formBo : formsList) {
      formInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.FORM,
              customIdsMap.get(INSTRUCTION_FORM_ID + formBo.getFormId()),
              formBo.getActive(),
              formBo.getCreatedBy(),
              formBo.getCreatedOn(),
              formBo.getModifiedBy(),
              formBo.getModifiedOn());
      formBoInsertQueryList.add(formInsertQuery);
    }
    insertSqlStatements.addAll(formBoInsertQueryList);
  }

  private void addComprehensionTestResponseBoListInsertSql(
      List<ComprehensionTestResponseBo> comprehensionTestResponseBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {
    if (CollectionUtils.isEmpty(comprehensionTestResponseBoList)) {
      return;
    }

    List<String> comprehensionTestResponseBoInserQueryList = new ArrayList<>();
    for (ComprehensionTestResponseBo comprehensionTestResponseBo :
        comprehensionTestResponseBoList) {
      String comprehensionTestResponseInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.COMPREHENSION_TEST_RESPONSE,
              IdGenerator.id(),
              customIdsMap.get(
                  COMPREHENSION_TEST_QUESTION_ID
                      + comprehensionTestResponseBo.getComprehensionTestQuestionId()),
              comprehensionTestResponseBo.getCorrectAnswer(),
              comprehensionTestResponseBo.getResponseOption(),
              comprehensionTestResponseBo.getSequenceNumber());

      comprehensionTestResponseBoInserQueryList.add(comprehensionTestResponseInsertQuery);
    }
    insertSqlStatements.addAll(comprehensionTestResponseBoInserQueryList);
  }

  public String saveFileToCloudStorage(StudyBo studyBo, List<String> insertSqlStatements) {
    StringBuilder content = new StringBuilder();
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      for (String insertSqlStatement : insertSqlStatements) {
        if (StringUtils.isNotEmpty(insertSqlStatement)) {
          content.append(insertSqlStatement);
          content.append(System.lineSeparator());
        }
      }

      byte[] bytes = content.toString().getBytes();
      Map<String, String> map = FdahpStudyDesignerUtil.getAppProperties();

      Session session = hibernateTemplate.getSessionFactory().openSession();

      studyBo.setExportSqlByte(bytes);
      study.getResourcesFromStorage(session, studyBo);

      String signedUrl =
          FdahpStudyDesignerUtil.getSignedUrlForExportedStudy(
              UNDER_DIRECTORY + PATH_SEPARATOR + studyBo.getCustomStudyId() + ".zip", 12);

      message = studyDao.saveExportFilePath(studyBo.getId(), studyBo.getCustomStudyId(), signedUrl);

      if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)
          && StringUtils.isNotEmpty(signedUrl)) {
        return FdahpStudyDesignerConstants.SUCCESS;
      }

    } catch (Exception e) {
      logger.error("Save file to cloud storage failed", e);
      return e.getMessage();
    }
    return message;
  }

  public long getCRC32Checksum(byte[] bytes) {
    Checksum crc32 = new CRC32();
    crc32.update(bytes, 0, bytes.length);
    return crc32.getValue();
  }

  private void addActiveTaskFrequencyBoInsertSqlQuery(
      List<ActiveTaskFrequencyBo> activeTaskFrequencyBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {
    if (CollectionUtils.isEmpty(activeTaskFrequencyBoList)) {
      return;
    }

    List<String> activeTaskBoInsertQueryList = new ArrayList<>();
    String activeTaskBoInsertQuery = null;
    for (ActiveTaskFrequencyBo activeTaskFrquencyBo : activeTaskFrequencyBoList) {
      activeTaskBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.ACTIVETASK_FREQUENCIES,
              IdGenerator.id(),
              customIdsMap.get(ACTIVETASK_ID + activeTaskFrquencyBo.getActiveTaskId()),
              activeTaskFrquencyBo.getFrequencyDate(),
              activeTaskFrquencyBo.getFrequencyTime(),
              activeTaskFrquencyBo.getIsLaunchStudy(),
              activeTaskFrquencyBo.getIsStudyLifeTime(),
              activeTaskFrquencyBo.getTimePeriodFromDays(),
              activeTaskFrquencyBo.getTimePeriodToDays(),
              activeTaskFrquencyBo.isxDaysSign(),
              activeTaskFrquencyBo.isyDaysSign(),
              activeTaskFrquencyBo.getSequenceNumber());

      activeTaskBoInsertQueryList.add(activeTaskBoInsertQuery);
    }
    insertSqlStatements.addAll(activeTaskBoInsertQueryList);
  }

  private void addActiveTaskCustomScheduleBoInsertSqlQuery(
      List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    if (CollectionUtils.isEmpty(activeTaskCustomScheduleBoList)) {
      return;
    }

    List<String> activeTaskCustomScheduleBoInsertQueryList = new ArrayList<>();
    String activeTaskCustomScheduleBoInsertQuery = null;
    for (ActiveTaskCustomScheduleBo activeTaskCustomScheduleBo : activeTaskCustomScheduleBoList) {
      activeTaskCustomScheduleBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.ACTIVETASK_CUSTOM_FREQUENCIES,
              IdGenerator.id(),
              customIdsMap.get(ACTIVETASK_ID + activeTaskCustomScheduleBo.getActiveTaskId()),
              activeTaskCustomScheduleBo.getFrequencyEndDate(),
              activeTaskCustomScheduleBo.getFrequencyStartDate(),
              activeTaskCustomScheduleBo.getTimePeriodFromDays(),
              activeTaskCustomScheduleBo.getTimePeriodToDays(),
              "N", // setting isUsed value to false
              activeTaskCustomScheduleBo.isxDaysSign(),
              activeTaskCustomScheduleBo.isyDaysSign(),
              activeTaskCustomScheduleBo.getFrequencyStartTime(),
              activeTaskCustomScheduleBo.getFrequencyEndTime(),
              activeTaskCustomScheduleBo.getSequenceNumber());

      activeTaskCustomScheduleBoInsertQueryList.add(activeTaskCustomScheduleBoInsertQuery);
    }
    insertSqlStatements.addAll(activeTaskCustomScheduleBoInsertQueryList);
  }

  private void addQuestionsResponseTypeInsertSql(
      List<QuestionReponseTypeBo> questionResponseTypeBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    if (CollectionUtils.isEmpty(questionResponseTypeBoList)) {
      return;
    }

    String questionResponseTypeBoInsertQuery = null;
    List<String> questionResponseTypeBoInsertQueryList = new ArrayList<>();
    for (QuestionReponseTypeBo questionResponseTypeBo : questionResponseTypeBoList) {
      questionResponseTypeBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.RESPONSE_TYPE_VALUE,
              customIdsMap.get(INSTRUCTION_FORM_ID + questionResponseTypeBo.getResponseTypeId()),
              questionResponseTypeBo.getActive(),
              questionResponseTypeBo.getConditionFormula(),
              questionResponseTypeBo.getDefaultDate(),
              questionResponseTypeBo.getDefaultTime(),
              questionResponseTypeBo.getDefaultValue(),
              questionResponseTypeBo.getFormulaBasedLogic(),
              questionResponseTypeBo.getImageSize(),
              questionResponseTypeBo.getInvalidMessage(),
              questionResponseTypeBo.getMaxDate(),
              questionResponseTypeBo.getMaxDescription(),
              questionResponseTypeBo.getMaxFractionDigits(),
              questionResponseTypeBo.getMaxImage(),
              questionResponseTypeBo.getMaxLength(),
              questionResponseTypeBo.getMaxValue(),
              questionResponseTypeBo.getMeasurementSystem(),
              questionResponseTypeBo.getMinDate(),
              questionResponseTypeBo.getMinDescription(),
              questionResponseTypeBo.getMinImage(),
              questionResponseTypeBo.getMinValue(),
              questionResponseTypeBo.getMultipleLines(),
              questionResponseTypeBo.getOtherDescription(),
              questionResponseTypeBo.getOtherDestinationStepId(),
              questionResponseTypeBo.getOtherExclusive(),
              questionResponseTypeBo.getOtherIncludeText(),
              questionResponseTypeBo.getOtherParticipantFill(),
              questionResponseTypeBo.getOtherPlaceholderText(),
              questionResponseTypeBo.getOtherText(),
              questionResponseTypeBo.getOtherType(),
              questionResponseTypeBo.getOtherValue(),
              questionResponseTypeBo.getPlaceholder(),
              customIdsMap.get(
                  INSTRUCTION_FORM_ID + questionResponseTypeBo.getQuestionsResponseTypeId()),
              questionResponseTypeBo.getSelectionStyle(),
              questionResponseTypeBo.getStep(),
              questionResponseTypeBo.getStyle(),
              questionResponseTypeBo.getTextChoices(),
              questionResponseTypeBo.getUnit(),
              questionResponseTypeBo.getUseCurrentLocation(),
              questionResponseTypeBo.getValidationCharacters(),
              questionResponseTypeBo.getValidationCondition(),
              questionResponseTypeBo.getValidationExceptText(),
              questionResponseTypeBo.getValidationRegex(),
              questionResponseTypeBo.getVertical());
      questionResponseTypeBoInsertQueryList.add(questionResponseTypeBoInsertQuery);
    }
    insertSqlStatements.addAll(questionResponseTypeBoInsertQueryList);
  }

  private void addQuestionsResponseSubTypeInsertSql(
      List<QuestionResponseSubTypeBo> questionResponseSubTypeBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap,
      Map<String, String> questionMap)
      throws Exception {

    if (CollectionUtils.isEmpty(questionResponseSubTypeBoList)) {
      return;
    }

    String questionResponseSubTypeBoInsertQuery = null;
    List<String> questionResponseSubTypeBoInsertQueryList = new ArrayList<>();
    for (QuestionResponseSubTypeBo questionResponseSubTypeBo : questionResponseSubTypeBoList) {
      questionResponseSubTypeBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.RESPONSE_SUB_TYPE_VALUE,
              customIdsMap.get(
                  INSTRUCTION_FORM_ID + questionResponseSubTypeBo.getResponseSubTypeValueId()),
              questionResponseSubTypeBo.getActive(),
              questionResponseSubTypeBo.getDescription(),
              StringUtils.isNotEmpty(questionResponseSubTypeBo.getDestinationStepId())
                      && questionResponseSubTypeBo.getDestinationStepId().equals(String.valueOf(0))
                  ? String.valueOf(0)
                  : questionMap.get(questionResponseSubTypeBo.getDestinationStepId()),
              questionResponseSubTypeBo.getDetail(),
              questionResponseSubTypeBo.getExclusive(),
              questionResponseSubTypeBo.getImage(),
              customIdsMap.get(INSTRUCTION_FORM_ID + questionResponseSubTypeBo.getResponseTypeId()),
              questionResponseSubTypeBo.getSelectedImage(),
              questionResponseSubTypeBo.getText(),
              questionResponseSubTypeBo.getValue(),
              questionResponseSubTypeBo.getSequenceNumber());

      questionResponseSubTypeBoInsertQueryList.add(questionResponseSubTypeBoInsertQuery);
    }
    insertSqlStatements.addAll(questionResponseSubTypeBoInsertQueryList);
  }

  private void addInstructionInsertSql(
      List<InstructionsBo> instructionList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {
    List<String> instructionBoInsertQueryList = new ArrayList<>();
    if (CollectionUtils.isEmpty(instructionList)) {
      return;
    }

    String instructionBoInsertQuery = null;
    for (InstructionsBo instructionBo : instructionList) {
      instructionBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.INSTRUCTION,
              customIdsMap.get(INSTRUCTION_FORM_ID + instructionBo.getId()),
              instructionBo.getActive(),
              instructionBo.getCreatedBy(),
              instructionBo.getCreatedOn(),
              instructionBo.getInstructionText(),
              instructionBo.getInstructionTitle(),
              instructionBo.getModifiedBy(),
              instructionBo.getModifiedOn(),
              instructionBo.getStatus());
      instructionBoInsertQueryList.add(instructionBoInsertQuery);
    }
    insertSqlStatements.addAll(instructionBoInsertQueryList);
  }

  private void addFormMappingListInsertSql(
      List<FormMappingBo> formsList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    List<String> formMappingBoInsertQueryList = new ArrayList<>();
    if (CollectionUtils.isEmpty(formsList)) {
      return;
    }

    String formMappingInsertQuery = null;
    for (FormMappingBo formMappingBo : formsList) {
      formMappingInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.FORM_MAPPING,
              customIdsMap.get(INSTRUCTION_FORM_ID + formMappingBo.getId()),
              formMappingBo.getActive(),
              customIdsMap.get(INSTRUCTION_FORM_ID + formMappingBo.getFormId()),
              customIdsMap.get(INSTRUCTION_FORM_ID + formMappingBo.getQuestionId()),
              formMappingBo.getSequenceNo());
      formMappingBoInsertQueryList.add(formMappingInsertQuery);
    }
    insertSqlStatements.addAll(formMappingBoInsertQueryList);
  }

  private void addQuestionListInsertSql(
      List<QuestionsBo> questionsList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    List<String> questionsBoInsertQueryList = new ArrayList<>();
    if (CollectionUtils.isEmpty(questionsList)) {
      return;
    }

    String questionInsertQuery = null;
    for (QuestionsBo questionBo : questionsList) {
      questionInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.QUESTIONS,
              customIdsMap.get(INSTRUCTION_FORM_ID + questionBo.getId()),
              questionBo.getActive(),
              questionBo.getAddLineChart(),
              questionBo.getAllowHealthKit(),
              questionBo.getAllowRollbackChart(),
              customIdsMap.get(ANCHORDATE_ID + questionBo.getAnchorDateId()),
              questionBo.getChartTitle(),
              questionBo.getCreatedBy(),
              questionBo.getCreatedOn(),
              questionBo.getDescription(),
              questionBo.getHealthkitDatatype(),
              questionBo.getLineChartTimeRange(),
              questionBo.getModifiedBy(),
              questionBo.getModifiedOn(),
              questionBo.getQuestion(),
              questionBo.getResponseType(),
              questionBo.getShortTitle(),
              questionBo.getSkippable(),
              questionBo.getStatDisplayName(),
              questionBo.getStatDisplayUnits(),
              questionBo.getStatFormula(),
              questionBo.getStatShortName(),
              questionBo.getStatType(),
              questionBo.getStatus(),
              questionBo.getUseAnchorDate(),
              questionBo.getUseStasticData());
      questionsBoInsertQueryList.add(questionInsertQuery);
    }
    insertSqlStatements.addAll(questionsBoInsertQueryList);
  }

  private void addQuestionnaireCustomScheduleBoInsertSql(
      List<QuestionnaireCustomScheduleBo> questionnairesCustomFrequenciesBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    List<String> questionnairesCustomScheduleBoInsertQueryList = new ArrayList<>();
    if (CollectionUtils.isEmpty(questionnairesCustomFrequenciesBoList)) {
      return;
    }

    String questionnairesCustomScheduleBoInsertQuery = null;
    for (QuestionnaireCustomScheduleBo questionnaireCustomScheduleBo :
        questionnairesCustomFrequenciesBoList) {
      questionnairesCustomScheduleBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.QUESTIONNAIRES_CUSTOM_FREQUENCIES,
              IdGenerator.id(),
              questionnaireCustomScheduleBo.getFrequencyEndDate(),
              questionnaireCustomScheduleBo.getFrequencyStartDate(),
              customIdsMap.get(
                  QUESTIONNAIRES_ID + questionnaireCustomScheduleBo.getQuestionnairesId()),
              questionnaireCustomScheduleBo.getTimePeriodFromDays(),
              questionnaireCustomScheduleBo.getTimePeriodToDays(),
              "N", // setting isUsed value to false
              questionnaireCustomScheduleBo.isxDaysSign(),
              questionnaireCustomScheduleBo.isyDaysSign(),
              questionnaireCustomScheduleBo.getFrequencyEndTime(),
              questionnaireCustomScheduleBo.getFrequencyStartTime(),
              questionnaireCustomScheduleBo.getSequenceNumber());

      questionnairesCustomScheduleBoInsertQueryList.add(questionnairesCustomScheduleBoInsertQuery);
    }
    insertSqlStatements.addAll(questionnairesCustomScheduleBoInsertQueryList);
  }

  private void addQuestionnairesStepsListInsertSql(
      List<QuestionnairesStepsBo> questionnairesStepsList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap,
      Map<String, String> questionMap)
      throws Exception {
    if (CollectionUtils.isEmpty(questionnairesStepsList)) {
      return;
    }
    String questionnaireStepsBoInsertQuery = null;
    List<String> questionnaireStepsBoInsertQueryList = new ArrayList<>();
    for (QuestionnairesStepsBo questionnairesStepsBo : questionnairesStepsList) {
      questionnaireStepsBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.QUESTIONNAIRES_STEPS,
              questionMap.get(questionnairesStepsBo.getStepId()),
              questionnairesStepsBo.getActive(),
              questionnairesStepsBo.getCreatedBy(),
              questionnairesStepsBo.getCreatedOn(),
              questionnairesStepsBo.getDestinationStep().equals(String.valueOf(0))
                  ? String.valueOf(0)
                  : questionMap.get(questionnairesStepsBo.getDestinationStep()),
              customIdsMap.get(INSTRUCTION_FORM_ID + questionnairesStepsBo.getInstructionFormId()),
              questionnairesStepsBo.getModifiedBy(),
              questionnairesStepsBo.getModifiedOn(),
              customIdsMap.get(QUESTIONNAIRES_ID + questionnairesStepsBo.getQuestionnairesId()),
              questionnairesStepsBo.getRepeatable(),
              questionnairesStepsBo.getRepeatableText(),
              questionnairesStepsBo.getSequenceNo(),
              questionnairesStepsBo.getSkiappable(),
              questionnairesStepsBo.getStatus(),
              questionnairesStepsBo.getStepShortTitle(),
              questionnairesStepsBo.getStepType());

      questionnaireStepsBoInsertQueryList.add(questionnaireStepsBoInsertQuery);
    }
    insertSqlStatements.addAll(questionnaireStepsBoInsertQueryList);
  }

  private void addStudiesInsertSql(
      StudyBo studyBo, List<String> insertSqlStatements, Map<String, String> customIdsMap)
      throws Exception {

    if (studyBo == null) {
      return;
    }

    String studiesInsertQuery =
        prepareInsertQuery(
            StudyExportSqlQueries.STUDIES,
            customIdsMap.get(STUDY_ID + studyBo.getId()),
            null,
            studyBo.getCategory(),
            studyBo.getCreatedBy(),
            FdahpStudyDesignerUtil.getCurrentDateTime(),
            customIdsMap.get(CUSTOM_STUDY_ID + studyBo.getCustomStudyId()),
            studyBo.getDescription(),
            YES,
            studyBo.getFullName(),
            studyBo.getHasActivetaskDraft(),
            studyBo.getHasActivityDraft(),
            studyBo.getHasConsentDraft(),
            studyBo.getHasQuestionnaireDraft(),
            studyBo.getHasStudyDraft(),
            studyBo.getInboxEmailAddress(),
            studyBo.getIrbReview(),
            0,
            studyBo.getMediaLink(),
            studyBo.getModifiedBy(),
            studyBo.getModifiedOn(),
            IMPORTED + studyBo.getName(),
            studyBo.getPlatform(),
            studyBo.getResearchSponsor(),
            studyBo.getSequenceNumber(),
            PRE_LAUNCH,
            studyBo.isStudyPreActiveFlag() ? "Y" : "N",
            studyBo.getStudyTagLine(),
            studyBo.getStudyWebsite(),
            studyBo.getStudylunchDate(),
            studyBo.getTentativeDuration(),
            studyBo.getTentativeDurationWeekmonth(),
            studyBo.getThumbnailImage(),
            studyBo.getType(),
            0f,
            studyBo.isEnrollmentdateAsAnchordate() ? "Y" : "N",
            studyBo.getCustomStudyId() + "@Export",
            null);

    insertSqlStatements.add(studiesInsertQuery);
  }

  private void addStudySequenceInsertSql(
      StudySequenceBo studySequenceBo,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    if (studySequenceBo == null) {
      return;
    }

    String studySequeneInsertQuery =
        prepareInsertQuery(
            StudyExportSqlQueries.STUDY_SEQUENCE,
            IdGenerator.id(),
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            "N",
            customIdsMap.get(STUDY_ID + studySequenceBo.getStudyId()));
    insertSqlStatements.add(studySequeneInsertQuery);
  }

  private void addAnchorDateInsertSql(
      List<AnchorDateTypeBo> anchorDateList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap,
      String studyId)
      throws Exception {

    if (CollectionUtils.isEmpty(anchorDateList)) {
      return;
    }

    List<String> anchorDateInsertQueryList = new ArrayList<>();
    for (AnchorDateTypeBo anchorDate : anchorDateList) {
      String anchorDateTypeInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.ANCHORDATE_TYPE,
              customIdsMap.get(ANCHORDATE_ID + anchorDate.getId()),
              customIdsMap.get(CUSTOM_STUDY_ID + anchorDate.getCustomStudyId()),
              anchorDate.getHasAnchortypeDraft(),
              anchorDate.getName(),
              customIdsMap.get(STUDY_ID + studyId),
              anchorDate.getVersion());

      anchorDateInsertQueryList.add(anchorDateTypeInsertQuery);
    }
    insertSqlStatements.addAll(anchorDateInsertQueryList);
  }

  private void addStudypagesListInsertSql(
      List<StudyPageBo> studypageList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    if (CollectionUtils.isEmpty(studypageList)) {
      return;
    }

    List<String> studyPageBoInsertQueryList = new ArrayList<>();
    Integer sequenceNumber = 0;
    for (StudyPageBo studyPageBo : studypageList) {
      String studyPageBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.STUDY_PAGE,
              IdGenerator.id(),
              studyPageBo.getCreatedBy(),
              FdahpStudyDesignerUtil.getCurrentDateTime(),
              studyPageBo.getDescription(),
              studyPageBo.getImagePath(),
              studyPageBo.getModifiedBy(),
              studyPageBo.getModifiedOn(),
              customIdsMap.get(STUDY_ID + studyPageBo.getStudyId()),
              studyPageBo.getTitle(),
              sequenceNumber++);

      studyPageBoInsertQueryList.add(studyPageBoInsertQuery);
    }
    insertSqlStatements.addAll(studyPageBoInsertQueryList);
  }

  private void addEligibilityInsertSql(
      EligibilityBo eligibilityBo,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    if (eligibilityBo == null) {
      return;
    }

    String eligibilityInsertQuery =
        prepareInsertQuery(
            StudyExportSqlQueries.ELIGIBILITY,
            customIdsMap.get(NEW_ELIGIBILITY_ID + eligibilityBo.getId()),
            eligibilityBo.getCreatedBy(),
            FdahpStudyDesignerUtil.getCurrentDateTime(),
            eligibilityBo.getEligibilityMechanism(),
            eligibilityBo.getFailureOutcomeText(),
            eligibilityBo.getInstructionalText(),
            eligibilityBo.getModifiedBy(),
            eligibilityBo.getModifiedOn(),
            customIdsMap.get(STUDY_ID + eligibilityBo.getStudyId()));

    insertSqlStatements.add(eligibilityInsertQuery);
  }

  private void addNotificationInsertSql(
      List<NotificationBO> notificationBOs,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap,
      String copyVersion,
      StudyBo studyBo)
      throws Exception {

    if (CollectionUtils.isEmpty(notificationBOs)) {
      return;
    }
    List<String> notificationBoBoInsertQueryList = new ArrayList<>();
    Integer sequenceNumber = 0;
    for (NotificationBO notificationBO : notificationBOs) {

      boolean flag = false;
      if (copyVersion.equals(PUBLISHED_VERSION)) {
        flag =
            notificationBO.getCreatedOn() == null
                ? true
                : Timestamp.valueOf(notificationBO.getCreatedOn())
                    .before(Timestamp.valueOf(studyBo.getStudylunchDate()));
      }

      if (copyVersion.equals(WORKING_VERSION) || (copyVersion.equals(PUBLISHED_VERSION) && flag)) {
        String notificationBoInsertQuery;
        notificationBoInsertQuery =
            prepareInsertQuery(
                StudyExportSqlQueries.NOTIFICATION,
                IdGenerator.id(),
                customIdsMap.get(ACTIVETASK_ID + notificationBO.getActiveTaskId()),
                notificationBO.isAnchorDate(),
                notificationBO.getAppId(),
                notificationBO.getCreatedBy(),
                notificationBO.getCreatedOn(),
                customIdsMap.get(CUSTOM_STUDY_ID + notificationBO.getCustomStudyId()),
                notificationBO.getModifiedBy(),
                notificationBO.getModifiedOn(),
                notificationBO.isNotificationStatus()
                    ? notificationBO.isNotificationAction()
                    : false,
                notificationBO.isNotificationStatus() ? notificationBO.isNotificationDone() : false,
                NOTIFICATION_NOTIMMEDIATE,
                false,
                notificationBO.isNotificationStatus(),
                notificationBO.getNotificationSubType(),
                notificationBO.getNotificationText(),
                notificationBO.getNotificationType(),
                customIdsMap.get(QUESTIONNAIRES_ID + notificationBO.getQuestionnarieId()),
                notificationBO.getResourceId(),
                notificationBO.getScheduleDate(),
                notificationBO.getScheduleTime(),
                customIdsMap.get(STUDY_ID + studyBo.getId()),
                notificationBO.getxDays(),
                notificationBO.getScheduleTimestamp(),
                sequenceNumber++,
                studyBo.getPlatform());
        notificationBoBoInsertQueryList.add(notificationBoInsertQuery);
      }
    }
    insertSqlStatements.addAll(notificationBoBoInsertQueryList);
  }

  private void addStudyActiveTaskInsertSql(
      List<ActiveTaskBo> activeTaskBos,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap,
      String studyId)
      throws Exception {

    if (CollectionUtils.isEmpty(activeTaskBos)) {
      return;
    }
    List<String> activeTaskBoInsertQueryList = new ArrayList<>();
    String activeTaskBoInsertQuery = null;
    for (ActiveTaskBo activeTaskBo : activeTaskBos) {
      activeTaskBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.ACTIVETASK,
              customIdsMap.get(ACTIVETASK_ID + activeTaskBo.getId()),
              activeTaskBo.isAction(),
              activeTaskBo.getActive(),
              activeTaskBo.getActiveTaskLifetimeEnd(),
              activeTaskBo.getActiveTaskLifetimeStart(),
              customIdsMap.get(ANCHORDATE_ID + activeTaskBo.getAnchorDateId()),
              activeTaskBo.getCreatedBy(),
              activeTaskBo.getCreatedDate(),
              customIdsMap.get(CUSTOM_STUDY_ID + activeTaskBo.getCustomStudyId()),
              activeTaskBo.getDayOfTheWeek(),
              activeTaskBo.getDisplayName(),
              activeTaskBo.getDuration(),
              activeTaskBo.getFrequency(),
              activeTaskBo.getInstruction(),
              activeTaskBo.getIsChange(),
              0,
              activeTaskBo.getModifiedBy(),
              activeTaskBo.getModifiedDate(),
              activeTaskBo.getRepeatActiveTask(),
              activeTaskBo.getScheduleType(),
              activeTaskBo.getShortTitle(),
              customIdsMap.get(STUDY_ID + studyId),
              activeTaskBo.getTaskTypeId(),
              activeTaskBo.getTitle(),
              activeTaskBo.getVersion());

      activeTaskBoInsertQueryList.add(activeTaskBoInsertQuery);
    }
    insertSqlStatements.addAll(activeTaskBoInsertQueryList);
  }

  private void addActiveTaskAtrributeValuesInsertSql(
      List<ActiveTaskAtrributeValuesBo> activeTaskAttributeBos,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    if (CollectionUtils.isEmpty(activeTaskAttributeBos)) {
      return;
    }
    List<String> activeTaskAtrributeInsertQueryList = new ArrayList<>();
    String activeTaskAtrributeInsertQuery = null;
    for (ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo : activeTaskAttributeBos) {
      activeTaskAtrributeInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.ACTIVETASK_ATTRIBUTES_VALUES,
              IdGenerator.id(),
              activeTaskAtrributeValuesBo.getActive(),
              customIdsMap.get(ACTIVETASK_ID + activeTaskAtrributeValuesBo.getActiveTaskId()),
              activeTaskAtrributeValuesBo.getActiveTaskMasterAttrId(),
              activeTaskAtrributeValuesBo.isAddToLineChart() ? "Y" : "N",
              activeTaskAtrributeValuesBo.getAttributeVal(),
              activeTaskAtrributeValuesBo.getDisplayNameStat(),
              activeTaskAtrributeValuesBo.getDisplayUnitStat(),
              activeTaskAtrributeValuesBo.getFormulaAppliedStat(),
              activeTaskAtrributeValuesBo.getIdentifierNameStat(),
              activeTaskAtrributeValuesBo.getRollbackChat(),
              activeTaskAtrributeValuesBo.getTimeRangeChart(),
              activeTaskAtrributeValuesBo.getTimeRangeStat(),
              activeTaskAtrributeValuesBo.getTitleChat(),
              activeTaskAtrributeValuesBo.getUploadTypeStat(),
              activeTaskAtrributeValuesBo.isUseForStatistic() ? "Y" : "N");

      activeTaskAtrributeInsertQueryList.add(activeTaskAtrributeInsertQuery);
    }
    insertSqlStatements.addAll(activeTaskAtrributeInsertQueryList);
  }

  private void addResourceInsertSql(
      List<ResourceBO> resourceBOs,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    if (CollectionUtils.isEmpty(resourceBOs)) {
      return;
    }
    List<String> resourceBoInsertQueryList = new ArrayList<>();
    for (ResourceBO resourceBO : resourceBOs) {
      String resourceBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.RESOURCES,
              IdGenerator.id(),
              resourceBO.isAction(),
              customIdsMap.get(ANCHORDATE_ID + resourceBO.getAnchorDateId()),
              resourceBO.getCreatedBy(),
              resourceBO.getCreatedOn(),
              resourceBO.getEndDate(),
              resourceBO.getModifiedBy(),
              resourceBO.getModifiedOn(),
              resourceBO.getPdfName(),
              resourceBO.getPdfUrl(),
              resourceBO.getResourceText(),
              resourceBO.isResourceType(),
              resourceBO.isResourceVisibility(),
              resourceBO.getRichText(),
              resourceBO.getSequenceNo(),
              resourceBO.getStartDate(),
              resourceBO.isStatus(),
              customIdsMap.get(STUDY_ID + resourceBO.getStudyId()),
              resourceBO.isStudyProtocol(),
              resourceBO.isTextOrPdf(),
              resourceBO.getTimePeriodFromDays(),
              resourceBO.getTimePeriodToDays(),
              resourceBO.getTitle(),
              resourceBO.isxDaysSign(),
              resourceBO.isyDaysSign());

      resourceBoInsertQueryList.add(resourceBoInsertQuery);
    }
    insertSqlStatements.addAll(resourceBoInsertQueryList);
  }

  private void addEligibilityTestListInsertSql(
      List<EligibilityTestBo> eligibilityTestBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {
    if (CollectionUtils.isEmpty(eligibilityTestBoList)) {
      return;
    }
    List<String> eligibilityTestBoInsertQueryList = new ArrayList<>();
    for (EligibilityTestBo eligibilityTestBo : eligibilityTestBoList) {
      eligibilityTestBo.setUsed(false);
      String eligibilityTestBoBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.ELIGIBILITY_TEST,
              IdGenerator.id(),
              eligibilityTestBo.getActive(),
              customIdsMap.get(NEW_ELIGIBILITY_ID + eligibilityTestBo.getEligibilityId()),
              eligibilityTestBo.getQuestion(),
              eligibilityTestBo.getResponseFormat(),
              eligibilityTestBo.getResponseNoOption(),
              eligibilityTestBo.getResponseYesOption(),
              eligibilityTestBo.getSequenceNo(),
              eligibilityTestBo.getShortTitle(),
              eligibilityTestBo.getStatus(),
              eligibilityTestBo.isUsed());

      eligibilityTestBoInsertQueryList.add(eligibilityTestBoBoInsertQuery);
    }
    insertSqlStatements.addAll(eligibilityTestBoInsertQueryList);
  }

  private void addConsentBoListInsertSql(
      List<ConsentBo> consentBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap,
      String studyId)
      throws Exception {

    if (CollectionUtils.isEmpty(consentBoList)) {
      return;
    }

    List<String> consentBoListInsertQuery = new ArrayList<>();
    for (ConsentBo consentBo : consentBoList) {
      String consentInsertSql =
          prepareInsertQuery(
              StudyExportSqlQueries.CONSENT,
              IdGenerator.id(),
              consentBo.getAllowWithoutPermission(),
              consentBo.getComprehensionTestMinimumScore(),
              consentBo.getConsentDocContent(),
              consentBo.getConsentDocType(),
              consentBo.getCreatedBy(),
              consentBo.getCreatedOn(),
              customIdsMap.get(CUSTOM_STUDY_ID + consentBo.getCustomStudyId()),
              consentBo.geteConsentAgree(),
              consentBo.geteConsentDatetime(),
              consentBo.geteConsentFirstName(),
              consentBo.geteConsentLastName(),
              consentBo.geteConsentSignature(),
              consentBo.getHtmlConsent(),
              consentBo.getLearnMoreText(),
              0,
              consentBo.getLongDescription(),
              consentBo.getModifiedBy(),
              consentBo.getModifiedOn(),
              consentBo.getNeedComprehensionTest(),
              consentBo.getShareDataPermissions(),
              consentBo.getShortDescription(),
              customIdsMap.get(STUDY_ID + studyId),
              consentBo.getTaglineDescription(),
              consentBo.getTitle(),
              0f,
              consentBo.getEnrollAgain());
      consentBoListInsertQuery.add(consentInsertSql);
    }
    insertSqlStatements.addAll(consentBoListInsertQuery);
  }

  private void addConsentInfoBoListInsertSql(
      List<ConsentInfoBo> consentInfoBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap,
      String studyId)
      throws Exception {

    if (CollectionUtils.isEmpty(consentInfoBoList)) {
      return;
    }

    List<String> consentInfoInsertQueryList = new ArrayList<>();
    for (ConsentInfoBo consentInfoBo : consentInfoBoList) {
      String consentInfoInsertSql =
          prepareInsertQuery(
              StudyExportSqlQueries.CONSENT_INFO,
              IdGenerator.id(),
              consentInfoBo.getActive(),
              consentInfoBo.getBriefSummary(),
              consentInfoBo.getConsentItemTitleId(),
              consentInfoBo.getConsentItemType(),
              consentInfoBo.getContentType(),
              consentInfoBo.getCreatedBy(),
              consentInfoBo.getCreatedOn(),
              customIdsMap.get(CUSTOM_STUDY_ID + consentInfoBo.getCustomStudyId()),
              consentInfoBo.getDisplayTitle(),
              consentInfoBo.getElaborated(),
              consentInfoBo.getHtmlContent(),
              0,
              consentInfoBo.getModifiedBy(),
              consentInfoBo.getModifiedOn(),
              consentInfoBo.getSequenceNo(),
              consentInfoBo.getStatus(),
              customIdsMap.get(STUDY_ID + studyId),
              consentInfoBo.getUrl(),
              0f,
              consentInfoBo.getVisualStep());

      consentInfoInsertQueryList.add(consentInfoInsertSql);
    }
    insertSqlStatements.addAll(consentInfoInsertQueryList);
  }

  private void addComprehensionTestQuestionListInsertSql(
      List<ComprehensionTestQuestionBo> comprehensionTestQuestionList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    if (CollectionUtils.isEmpty(comprehensionTestQuestionList)) {
      return;
    }

    List<String> comprehensionTestQuestionInsertQueryList = new ArrayList<>();
    for (ComprehensionTestQuestionBo comprehensionTestQuestionBo : comprehensionTestQuestionList) {
      String comprehensionTestQuestionInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.COMPREHENSION_TEST_QUESTIONS,
              customIdsMap.get(
                  COMPREHENSION_TEST_QUESTION_ID + comprehensionTestQuestionBo.getId()),
              comprehensionTestQuestionBo.getActive(),
              comprehensionTestQuestionBo.getCreatedBy(),
              comprehensionTestQuestionBo.getCreatedOn(),
              comprehensionTestQuestionBo.getModifiedBy(),
              comprehensionTestQuestionBo.getModifiedOn(),
              comprehensionTestQuestionBo.getQuestionText(),
              comprehensionTestQuestionBo.getSequenceNo(),
              comprehensionTestQuestionBo.getStatus(),
              comprehensionTestQuestionBo.getStructureOfCorrectAns(),
              customIdsMap.get(STUDY_ID + comprehensionTestQuestionBo.getStudyId()));

      comprehensionTestQuestionInsertQueryList.add(comprehensionTestQuestionInsertQuery);
    }
    insertSqlStatements.addAll(comprehensionTestQuestionInsertQueryList);
  }

  private void addQuestionnaireBoListInsertSql(
      List<QuestionnaireBo> questionnairesList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap,
      StudyBo studyBo)
      throws Exception {

    if (CollectionUtils.isEmpty(questionnairesList)) {
      return;
    }

    List<String> questionnairesBoInsertQueryList = new ArrayList<>();
    for (QuestionnaireBo questionnaireBo : questionnairesList) {
      String questionnairesBoInsertQuery = null;
      questionnairesBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.QUESTIONNAIRES,
              customIdsMap.get(QUESTIONNAIRES_ID + questionnaireBo.getId()),
              questionnaireBo.getActive(),
              customIdsMap.get(ANCHORDATE_ID + questionnaireBo.getAnchorDateId()),
              questionnaireBo.getBranching(),
              questionnaireBo.getCreatedBy(),
              questionnaireBo.getCreatedDate(),
              customIdsMap.get(CUSTOM_STUDY_ID + questionnaireBo.getCustomStudyId()),
              questionnaireBo.getDayOfTheWeek(),
              questionnaireBo.getFrequency(),
              1, // setting isChange value to 1
              0,
              questionnaireBo.getModifiedBy(),
              questionnaireBo.getModifiedDate(),
              questionnaireBo.getRepeatQuestionnaire(),
              questionnaireBo.getScheduleType(),
              questionnaireBo.getShortTitle(),
              questionnaireBo.getStatus(),
              customIdsMap.get(STUDY_ID + studyBo.getId()),
              questionnaireBo.getStudyLifetimeEnd(),
              questionnaireBo.getStudyLifetimeStart(),
              questionnaireBo.getTitle(),
              0f,
              questionnaireBo.getSequenceNumber());

      questionnairesBoInsertQueryList.add(questionnairesBoInsertQuery);
    }
    insertSqlStatements.addAll(questionnairesBoInsertQueryList);
  }

  private void addQuestionnaireFrequenciesBoInsertSql(
      List<QuestionnairesFrequenciesBo> questionnairesFrequenciesBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    List<String> questionnairesFrequenciesBoInsertQueryList = new ArrayList<>();
    if (CollectionUtils.isEmpty(questionnairesFrequenciesBoList)) {
      return;
    }
    String questionnairesFrequenciesBoInsertQuery = null;
    for (QuestionnairesFrequenciesBo questionnairesFrequenciesBo :
        questionnairesFrequenciesBoList) {
      questionnairesFrequenciesBoInsertQuery =
          prepareInsertQuery(
              StudyExportSqlQueries.QUESTIONNAIRES_FREQUENCIES,
              IdGenerator.id(),
              questionnairesFrequenciesBo.getFrequencyDate(),
              questionnairesFrequenciesBo.getFrequencyTime(),
              questionnairesFrequenciesBo.getIsLaunchStudy(),
              questionnairesFrequenciesBo.getIsStudyLifeTime(),
              customIdsMap.get(
                  QUESTIONNAIRES_ID + questionnairesFrequenciesBo.getQuestionnairesId()),
              questionnairesFrequenciesBo.getTimePeriodFromDays(),
              questionnairesFrequenciesBo.getTimePeriodToDays(),
              questionnairesFrequenciesBo.isxDaysSign(),
              questionnairesFrequenciesBo.isyDaysSign(),
              questionnairesFrequenciesBo.getSequenceNumber());
      questionnairesFrequenciesBoInsertQueryList.add(questionnairesFrequenciesBoInsertQuery);
    }

    insertSqlStatements.addAll(questionnairesFrequenciesBoInsertQueryList);
  }

  private String prepareInsertQuery(String sqlQuery, Object... values) throws Exception {
    logger.info(" begin prepareInsertQuery()");

    Object[] columns =
        sqlQuery
            .substring(sqlQuery.indexOf('(') + 1, sqlQuery.indexOf(")"))
            .replace("`", "")
            .split(",");
    try {
      if (columns.length != values.length) {
        throw new SQLException("Column count doesn't match value count.");
      }

      int i = 0;
      for (Object column : columns) {
        column = ((String) column).trim();
        if (column.equals("brief_summary")
            || column.equals("description")
            || column.equals("elaborated")
            || column.equals("rich_text")
            || column.equals("consent_doc_content")
            || column.equals("learn_more_text")) {
          String value = (String) values[i];
          if (StringUtils.isNotEmpty(value)) {
            values[i] = value.replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
          }
        }
        if (values[i] instanceof String || values[i] instanceof Timestamp) {
          sqlQuery =
              sqlQuery.replace(
                  "<" + column + ">", "'" + values[i].toString().replace("'", "\\'") + "'");
        } else {
          sqlQuery = sqlQuery.replace("<" + column + ">", "" + values[i] + "");
        }

        i++;
      }
      return sqlQuery;
    } catch (Exception e) {
      logger.error("export study failed due to %s", e);
      throw new SQLException(e.getMessage());
    }
  }

  private void getNewInstructionFormIds(
      List<QuestionsBo> questionsList,
      List<FormMappingBo> formMappingList,
      List<InstructionsBo> instructionList,
      List<FormBo> formsList,
      List<QuestionResponseSubTypeBo> questionResponseSubTypeBoList,
      List<QuestionReponseTypeBo> questionResponseTypeBoList,
      Map<String, String> customIdsMap) {

    if (CollectionUtils.isNotEmpty(questionsList)) {
      for (QuestionsBo questionsBo : questionsList) {
        customIdsMap.put(INSTRUCTION_FORM_ID + questionsBo.getId(), IdGenerator.id());
      }
    }

    if (CollectionUtils.isNotEmpty(formMappingList)) {
      for (FormMappingBo formMappingBo : formMappingList) {
        customIdsMap.put(INSTRUCTION_FORM_ID + formMappingBo.getId(), IdGenerator.id());
      }
    }

    if (CollectionUtils.isNotEmpty(instructionList)) {
      for (InstructionsBo instructionsBo : instructionList) {
        customIdsMap.put(INSTRUCTION_FORM_ID + instructionsBo.getId(), IdGenerator.id());
      }
    }

    if (CollectionUtils.isNotEmpty(questionResponseSubTypeBoList)) {
      for (QuestionResponseSubTypeBo questionResponseSubTypeBo : questionResponseSubTypeBoList) {
        customIdsMap.put(
            INSTRUCTION_FORM_ID + questionResponseSubTypeBo.getResponseSubTypeValueId(),
            IdGenerator.id());
      }
    }

    if (CollectionUtils.isNotEmpty(questionResponseTypeBoList)) {
      for (QuestionReponseTypeBo questionReponseTypeBo : questionResponseTypeBoList) {
        customIdsMap.put(
            INSTRUCTION_FORM_ID + questionReponseTypeBo.getResponseTypeId(), IdGenerator.id());
      }
    }

    if (CollectionUtils.isNotEmpty(formsList)) {
      for (FormBo formBo : formsList) {
        customIdsMap.put(INSTRUCTION_FORM_ID + formBo.getFormId(), IdGenerator.id());
      }
    }
  }

  @Transactional
  public String importStudy(String signedUrl, SessionObject sessionObject) throws Exception {
    logger.entry("StudyExportService - importStudy() - Starts");
    Map<String, String> map = FdahpStudyDesignerUtil.getAppProperties();
    BufferedReader bufferedReader = null;
    String sqlPath = null;
    try {
      HttpClient client = HttpClients.createDefault();
      HttpGet httpGet = new HttpGet(signedUrl);
      HttpResponse response = client.execute(httpGet);
      HttpEntity entity = response.getEntity();

      if (entity != null) {
        if (!entity.getContentType().getValue().contains("application/xml")) {
          String pathOfZipUrl = signedUrl.substring(0, signedUrl.indexOf(".zip"));
          String[] tokens = pathOfZipUrl.split("/");
          String customId = tokens[tokens.length - 1];

          InputStream input = entity.getContent();
          byte[] data = IOUtils.toByteArray(input);
          String path = writeFileLocalImport(data, customId);
          if (path != null) {
            Object[] obj = FdahpStudyDesignerUtil.unzip(path, customId);
            new File(path).deleteOnExit();
            sqlPath = (String) obj[0];
            bufferedReader = (BufferedReader) obj[1];
          }

        } else {
          throw new Exception(INVALID_URL);
        }
      }
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        return INVALID_URL;
      }
      return e.getMessage();
    }
    return validateAndExecuteQuries(sqlPath, map, bufferedReader, sessionObject.getUserId());
  }

  private String writeFileLocalImport(byte[] data, String customId) {
    ServletContext context = ServletContextHolder.getServletContext();
    File directoryOfExport = new File(context.getRealPath("/") + "/Import");
    if (!directoryOfExport.exists()) {
      directoryOfExport.mkdir();
    }
    String zipPath = context.getRealPath("/") + "/Import/" + customId + ".zip";
    Path path = Paths.get(zipPath);
    try {
      Files.write(path, data);
      return zipPath;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String validateAndExecuteQuries(
      String sqlPath, Map<String, String> map, BufferedReader bufferedReader, String userId)
      throws Exception {
    try {

      if (sqlPath != null && bufferedReader != null) {

        String path = sqlPath.substring(0, sqlPath.indexOf(".sql"));
        String[] tokens = path.split("_");
        long checksum = Long.parseLong(tokens[tokens.length - 1]);
        String version = tokens[tokens.length - 2];

        // validating release version
        ComparableVersion signedUrlVersion = new ComparableVersion(version);
        ComparableVersion releaseVersion = new ComparableVersion(map.get("release.version"));

        if (signedUrlVersion.compareTo(releaseVersion) > 0) {
          throw new Exception(
              IMPORT_FAILED_DUE_TO_INCOMPATIBLE_VERSION + " " + map.get("release.version") + ").");
        }

        // validating tableName and insert statements
        String line;
        StringBuilder content = new StringBuilder();
        List<String> insertStatements = new ArrayList<>();
        String[] allowedTablesName = StudyExportSqlQueries.ALLOWED_STUDY_TABLE_NAMES;

        while ((line = bufferedReader.readLine()) != null) {
          if (!line.startsWith("INSERT")) {
            throw new Exception(IMPORT_FAILED_DUE_TO_ANOMOLIES_DETECTED_IN_FILLE);
          }

          String tableName =
              line.substring(line.indexOf('`') + 1, line.indexOf('`', line.indexOf('`') + 1));
          if (!Arrays.asList(allowedTablesName).contains(tableName)) {
            throw new Exception(IMPORT_FAILED_DUE_TO_ANOMOLIES_DETECTED_IN_FILLE);
          }

          insertStatements.add(line);
          content.append(line);
          content.append(System.lineSeparator());
        }

        // validating checksum
        byte[] bytes = content.toString().getBytes();
        if (checksum != getCRC32Checksum(bytes)) {
          throw new Exception(IMPORT_FAILED_DUE_TO_ANOMOLIES_DETECTED_IN_FILLE);
        }

        // execution
        String insertStatementofStudy = "";
        for (String insert : insertStatements) {
          if (insert.startsWith("INSERT INTO `studies`")) {
            insertStatementofStudy = insert;
          }
          jdbcTemplate.execute(insert);
        }

        // study permission
        String[] values = insertStatementofStudy.split("VALUES");
        String StudyId =
            values[1].substring(values[1].indexOf("'") + 1, values[1].indexOf(",") - 1);
        studyDao.giveStudyPermission(StudyId, userId);
      } else {
        return "FAILURE";
      }
    } catch (Exception e) {
      logger.error("StudyExportService - importStudy() - ERROR ", e);
      if (e instanceof DuplicateKeyException) {
        return IMPORT_FAILED_DUE_TO_ALREADY_USED_URL;
      }
      return IMPORT_FAILED_DUE_TO_ANOMOLIES_DETECTED_IN_FILLE;
    }
    return SUCCESS;
  }
}
