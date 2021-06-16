package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.IMPORT_FAILED_DUE_TO_ALREADY_USED_URL;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.IMPORT_FAILED_DUE_TO_ANOMOLIES_DETECTED_IN_FILLE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.IMPORT_FAILED_DUE_TO_INCOMPATIBLE_VERSION;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.INVALID_URL;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.NOTIFICATION_NOTIMMEDIATE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.SUCCESS;
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
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import com.fdahpstudydesigner.dao.NotificationDAO;
import com.fdahpstudydesigner.dao.StudyActiveTasksDAO;
import com.fdahpstudydesigner.dao.StudyDAO;
import com.fdahpstudydesigner.dao.StudyQuestionnaireDAO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.IdGenerator;
import com.fdahpstudydesigner.util.SessionObject;
import com.fdahpstudydesigner.util.StudyExportSqlQueries;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.sql.DataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
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

  private static XLogger logger =
      XLoggerFactory.getXLogger(StudyExportImportService.class.getName());

  @Autowired private StudyDAO studyDao;

  @Autowired private StudyQuestionnaireDAO studyQuestionnaireDAO;

  @Autowired private NotificationDAO notificationDAO;

  @Autowired private StudyActiveTasksDAO studyActiveTasksDAO;

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private static final String PATH_SEPARATOR = "/";

  private static final String UNDER_DIRECTORY = "export-studies";

  public String exportStudy(String studyId, String userId, AuditLogEventRequest auditRequest) {

    final Map<String, String> customIdsMap = new HashMap<>();

    StudyBo studyBo = studyDao.getStudy(studyId);

    if (studyBo != null) {
      auditRequest.setStudyId(studyBo.getCustomStudyId());
      auditRequest.setStudyVersion(studyBo.getVersion().toString());
      auditRequest.setAppId(studyBo.getAppId());
      customIdsMap.put(STUDY_ID + studyBo.getId(), IdGenerator.id());
      customIdsMap.put(CUSTOM_STUDY_ID + studyBo.getCustomStudyId(), null);

      // StudyPermissionBO studyPermissionBo = studyDao.getStudyPermissionBO(studyBo.getId(),
      // userId);
      StudySequenceBo studySequenceBo = studyDao.getStudySequenceByStudyId(studyBo.getId());

      List<AnchorDateTypeBo> anchorDateList = studyDao.getAnchorDateDetails(studyBo.getId());
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

      List<ConsentBo> consentBoList = studyDao.getConsentListForStudy(studyBo.getId());

      List<ConsentInfoBo> consentInfoBoList = studyDao.getConsentInfoList(studyBo.getId());

      List<NotificationBO> notificationBOs = notificationDAO.getNotificationsList(studyBo.getId());

      List<ResourceBO> resourceBOs = studyDao.getResourceList(studyBo.getId());

      List<String> insertSqlStatements = new ArrayList<>();
      try {
        addStudiesInsertSql(studyBo, insertSqlStatements, customIdsMap);
        //  addStudyPermissionInsertSql(studyPermissionBo, insertSqlStatements, customIdsMap);
        addStudySequenceInsertSql(studySequenceBo, insertSqlStatements, customIdsMap);

        addAnchorDateInsertSql(anchorDateList, insertSqlStatements, customIdsMap);
        addStudypagesListInsertSql(studypageList, insertSqlStatements, customIdsMap);

        addEligibilityInsertSql(eligibilityBo, insertSqlStatements, customIdsMap);
        addEligibilityTestListInsertSql(eligibilityBoList, insertSqlStatements, customIdsMap);

        addConsentBoListInsertSql(consentBoList, insertSqlStatements, customIdsMap);
        addConsentInfoBoListInsertSql(consentInfoBoList, insertSqlStatements, customIdsMap);

        prepareInsertSqlQueriesForComprehensionTest(customIdsMap, insertSqlStatements, studyBo);

        prepareInsertSqlQueriesForQuestionnaires(customIdsMap, insertSqlStatements, studyBo);

        prepareInsertSqlQueriesForStudyActiveTasks(customIdsMap, insertSqlStatements, studyBo);

        addNotificationInsertSql(notificationBOs, insertSqlStatements, customIdsMap);

        addResourceInsertSql(resourceBOs, insertSqlStatements, customIdsMap);

        return saveFileToCloudStorage(studyBo, insertSqlStatements);

      } catch (Exception e) {
        logger.error(String.format("export study failed due to %s", e.getMessage()), e);
        return FdahpStudyDesignerConstants.EXPORT_FAILURE_MSG;
      }
    }
    return FdahpStudyDesignerConstants.EXPORT_FAILURE_MSG;
  }

  private void prepareInsertSqlQueriesForStudyActiveTasks(
      final Map<String, String> customIdsMap, List<String> insertSqlStatements, StudyBo studyBo)
      throws Exception {

    List<ActiveTaskBo> activeTaskBos =
        studyActiveTasksDAO.getStudyActiveTaskByStudyId(studyBo.getId());

    Map<String, List<ActiveTaskCustomScheduleBo>> activeTaskCustomFrequencyMap = new HashMap<>();
    List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleList = new ArrayList<>();
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
        }
      }
    }

    List<ActiveTaskCustomScheduleBo> activeTaskcustomFrequencyList = new ArrayList<>();
    for (Map.Entry<String, List<ActiveTaskCustomScheduleBo>> entry :
        activeTaskCustomFrequencyMap.entrySet()) {
      Integer seq = 0;
      for (ActiveTaskCustomScheduleBo activeTaskCustomScheduleBo : entry.getValue()) {
        activeTaskCustomScheduleBo.setSequenceNumber(seq++);
        activeTaskcustomFrequencyList.add(activeTaskCustomScheduleBo);
      }
    }

    List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos =
        studyActiveTasksDAO.getActiveTaskAtrributeValuesByActiveTaskId(activeTaskIds);

    /*List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleBoList =
    studyActiveTasksDAO.getActiveTaskCustomScheduleBoList(activeTaskIds);*/

    List<ActiveTaskFrequencyBo> activeTaskFrequencyBoList =
        studyActiveTasksDAO.getActiveTaskFrequencyBoList(activeTaskIds);

    addStudyActiveTaskInsertSql(activeTaskBos, insertSqlStatements, customIdsMap);

    addActiveTaskAtrributeValuesInsertSql(
        activeTaskAtrributeValuesBos, insertSqlStatements, customIdsMap);

    addActiveTaskCustomScheduleBoInsertSqlQuery(
        activeTaskcustomFrequencyList, insertSqlStatements, customIdsMap);

    addActiveTaskFrequencyBoInsertSqlQuery(
        activeTaskFrequencyBoList, insertSqlStatements, customIdsMap);
  }

  private void prepareInsertSqlQueriesForQuestionnaires(
      final Map<String, String> customIdsMap, List<String> insertSqlStatements, StudyBo studyBo)
      throws Exception {

    List<QuestionnaireBo> questionnairesList =
        studyQuestionnaireDAO.getStudyQuestionnairesByStudyId(studyBo.getId());

    Map<String, List<QuestionnaireCustomScheduleBo>> customScheduleMap = new HashMap<>();

    List<String> questionnaireIds = new ArrayList<>();
    Integer count = 0;
    if (CollectionUtils.isNotEmpty(questionnairesList)) {
      for (QuestionnaireBo questionnaireBo : questionnairesList) {
        questionnaireIds.add(questionnaireBo.getId());
        customIdsMap.put(QUESTIONNAIRES_ID + questionnaireBo.getId(), IdGenerator.id());
        questionnaireBo.setSequenceNumber(count++);

        if (questionnaireBo
            .getFrequency()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
          List<QuestionnaireCustomScheduleBo> list =
              studyQuestionnaireDAO.getQuestionnaireCustomSchedules(questionnaireBo.getId());
          customScheduleMap.put(questionnaireBo.getId(), list);
        }
      }
    }

    List<QuestionnaireCustomScheduleBo> customList = new ArrayList<>();
    for (Map.Entry<String, List<QuestionnaireCustomScheduleBo>> entry :
        customScheduleMap.entrySet()) {

      Integer seq = 0;
      for (QuestionnaireCustomScheduleBo questionnaireCustomScheduleBo : entry.getValue()) {
        questionnaireCustomScheduleBo.setSequnceNumber(seq++);
        customList.add(questionnaireCustomScheduleBo);
      }
    }

    List<QuestionnairesStepsBo> questionnairesStepsList =
        studyQuestionnaireDAO.getQuestionnairesStepsList(questionnaireIds);

    List<String> instructionFormIds = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(questionnairesStepsList)) {
      for (QuestionnairesStepsBo questionnairesStepsBo : questionnairesStepsList) {
        instructionFormIds.add(questionnairesStepsBo.getInstructionFormId());
      }
    }

    List<QuestionnairesFrequenciesBo> questionnairesFrequenciesBoList =
        studyQuestionnaireDAO.getQuestionnairesFrequenciesBoList(questionnaireIds);

    /*  List<QuestionnaireCustomScheduleBo> questionnairesCustomFrequenciesBoList =
    studyQuestionnaireDAO.getQuestionnairesCustomFrequenciesBoList(questionnaireIds);*/

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
      Integer seq = 0;
      for (QuestionResponseSubTypeBo questionResponseSubTypeBo : entry.getValue()) {
        questionResponseSubTypeBo.setSequenceNumber(seq++);
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

    addQuestionnaireBoListInsertSql(questionnairesList, insertSqlStatements, customIdsMap);

    addQuestionnaireFrequenciesBoInsertSql(
        questionnairesFrequenciesBoList, insertSqlStatements, customIdsMap);

    addQuestionnaireCustomScheduleBoInsertSql(customList, insertSqlStatements, customIdsMap);

    addQuestionListInsertSql(questionsList, insertSqlStatements, customIdsMap);

    addFormMappingListInsertSql(formMappingList, insertSqlStatements, customIdsMap);

    addFormsListInsertSql(formsList, insertSqlStatements, customIdsMap);

    addInstructionInsertSql(instructionList, insertSqlStatements, customIdsMap);

    addQuestionsResponseSubTypeInsertSql(responseList, insertSqlStatements, customIdsMap);

    addQuestionsResponseTypeInsertSql(questionResponseTypeBo, insertSqlStatements, customIdsMap);

    addQuestionnairesStepsListInsertSql(questionnairesStepsList, insertSqlStatements, customIdsMap);
  }

  private void prepareInsertSqlQueriesForComprehensionTest(
      final Map<String, String> customIdsMap, List<String> insertSqlStatements, StudyBo studyBo)
      throws Exception {

    List<ComprehensionTestQuestionBo> comprehensionTestQuestionBoList =
        studyDao.getComprehensionTestQuestionList(studyBo.getId());

    List<String> comprehensionTestQuestionIds = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(comprehensionTestQuestionBoList)) {
      for (ComprehensionTestQuestionBo comprehensionTestQuestionBo :
          comprehensionTestQuestionBoList) {
        comprehensionTestQuestionIds.add(comprehensionTestQuestionBo.getId());
        customIdsMap.put(
            COMPREHENSION_TEST_QUESTION_ID + comprehensionTestQuestionBo.getId(), IdGenerator.id());
      }
    }

    List<ComprehensionTestResponseBo> comprehensionTestResponseBoList =
        studyDao.getComprehensionTestResponseList(comprehensionTestQuestionIds);

    addComprehensionTestQuestionListInsertSql(
        comprehensionTestQuestionBoList, insertSqlStatements, customIdsMap);

    addComprehensionTestResponseBoListInsertSql(
        comprehensionTestResponseBoList, insertSqlStatements, customIdsMap);
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
              comprehensionTestResponseBo.getResponseOption());

      comprehensionTestResponseBoInserQueryList.add(comprehensionTestResponseInsertQuery);
    }
    insertSqlStatements.addAll(comprehensionTestResponseBoInserQueryList);
  }

  public String saveFileToCloudStorage(StudyBo studyBo, List<String> insertSqlStatements) {
    StringBuilder content = new StringBuilder();
    try {
      for (String insertSqlStatement : insertSqlStatements) {
        if (StringUtils.isNotEmpty(insertSqlStatement)) {
          content.append(insertSqlStatement);
          content.append(System.lineSeparator());
        }
      }

      Map<String, String> map = FdahpStudyDesignerUtil.getAppProperties();
      byte[] bytes = content.toString().getBytes();
      String fileName =
          studyBo.getId()
              + "_"
              + map.get("release.version")
              + "_"
              + getCRC32Checksum(bytes)
              + ".sql";

      String absoluteFileName = UNDER_DIRECTORY + PATH_SEPARATOR + fileName;

      Storage storage = StorageOptions.getDefaultInstance().getService();
      BlobInfo blobInfo =
          BlobInfo.newBuilder(map.get("cloud.bucket.name.export.studies"), absoluteFileName)
              .build();
      storage.create(blobInfo, bytes);

      String signedUrl =
          FdahpStudyDesignerUtil.getSignedUrlForExportedStudy(
              absoluteFileName, Integer.parseInt(map.get("signed.url.expiration.in.hour")));

      String message =
          studyDao.saveExportFilePath(studyBo.getId(), studyBo.getCustomStudyId(), signedUrl);

      if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)
          && StringUtils.isNotEmpty(signedUrl)) {
        return FdahpStudyDesignerConstants.SUCCESS;
      }

    } catch (Exception e) {
      logger.error("Save file to cloud storage failed", e);
      return e.getMessage();
    }
    return null;
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
              activeTaskFrquencyBo.isyDaysSign());

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
      Map<String, String> customIdsMap)
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
              questionResponseSubTypeBo.getDestinationStepId(),
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
              questionnaireCustomScheduleBo.getSequnceNumber());
      questionnairesCustomScheduleBoInsertQueryList.add(questionnairesCustomScheduleBoInsertQuery);
    }
    insertSqlStatements.addAll(questionnairesCustomScheduleBoInsertQueryList);
  }

  private void addQuestionnairesStepsListInsertSql(
      List<QuestionnairesStepsBo> questionnairesStepsList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
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
              IdGenerator.id(),
              questionnairesStepsBo.getActive(),
              questionnairesStepsBo.getCreatedBy(),
              questionnairesStepsBo.getCreatedOn(),
              questionnairesStepsBo.getDestinationStep(),
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
            studyBo.getLive(),
            studyBo.getMediaLink(),
            studyBo.getModifiedBy(),
            studyBo.getModifiedOn(),
            studyBo.getName(),
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
            studyBo.getVersion(),
            studyBo.isEnrollmentdateAsAnchordate() ? "Y" : "N",
            studyBo.getCustomStudyId(),
            studyBo.getExportSignedUrl());

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
      Map<String, String> customIdsMap)
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
              customIdsMap.get(STUDY_ID + anchorDate.getStudyId()),
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
              studyPageBo.getSequenceNumber());

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
      Map<String, String> customIdsMap)
      throws Exception {

    if (CollectionUtils.isEmpty(notificationBOs)) {
      return;
    }
    List<String> notificationBoBoInsertQueryList = new ArrayList<>();
    Integer sequence = 0;
    for (NotificationBO notificationBO : notificationBOs) {
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
              notificationBO.isNotificationStatus() ? notificationBO.isNotificationAction() : false,
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
              customIdsMap.get(STUDY_ID + notificationBO.getStudyId()),
              notificationBO.getxDays(),
              notificationBO.getScheduleTimestamp(),
              sequence++);
      notificationBoBoInsertQueryList.add(notificationBoInsertQuery);
    }
    insertSqlStatements.addAll(notificationBoBoInsertQueryList);
  }

  private void addStudyActiveTaskInsertSql(
      List<ActiveTaskBo> activeTaskBos,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
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
              activeTaskBo.getLive(),
              activeTaskBo.getModifiedBy(),
              activeTaskBo.getModifiedDate(),
              activeTaskBo.getRepeatActiveTask(),
              activeTaskBo.getScheduleType(),
              activeTaskBo.getShortTitle(),
              customIdsMap.get(STUDY_ID + activeTaskBo.getStudyId()),
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

  private void addStudyPermissionInsertSql(
      StudyPermissionBO studyPermissionBo,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
      throws Exception {

    if (studyPermissionBo == null) {
      return;
    }

    String studyPermissionsInsertQuery =
        prepareInsertQuery(
            StudyExportSqlQueries.STUDY_PERMISSION,
            IdGenerator.id(),
            studyPermissionBo.getDelFlag(),
            studyPermissionBo.getProjectLead(),
            customIdsMap.get(STUDY_ID + studyPermissionBo.getStudyId()),
            studyPermissionBo.getUserId(),
            studyPermissionBo.isViewPermission());

    insertSqlStatements.add(studyPermissionsInsertQuery);
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
              eligibilityTestBo.isUsed() ? "Y" : "N");

      eligibilityTestBoInsertQueryList.add(eligibilityTestBoBoInsertQuery);
    }
    insertSqlStatements.addAll(eligibilityTestBoInsertQueryList);
  }

  private void addConsentBoListInsertSql(
      List<ConsentBo> consentBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
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
              consentBo.getLive(),
              consentBo.getLongDescription(),
              consentBo.getModifiedBy(),
              consentBo.getModifiedOn(),
              consentBo.getNeedComprehensionTest(),
              consentBo.getShareDataPermissions(),
              consentBo.getShortDescription(),
              customIdsMap.get(STUDY_ID + consentBo.getStudyId()),
              consentBo.getTaglineDescription(),
              consentBo.getTitle(),
              consentBo.getVersion(),
              consentBo.getEnrollAgain());
      consentBoListInsertQuery.add(consentInsertSql);
    }
    insertSqlStatements.addAll(consentBoListInsertQuery);
  }

  private void addConsentInfoBoListInsertSql(
      List<ConsentInfoBo> consentInfoBoList,
      List<String> insertSqlStatements,
      Map<String, String> customIdsMap)
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
              consentInfoBo.getLive(),
              consentInfoBo.getModifiedBy(),
              consentInfoBo.getModifiedOn(),
              consentInfoBo.getSequenceNo(),
              consentInfoBo.getStatus(),
              customIdsMap.get(STUDY_ID + consentInfoBo.getStudyId()),
              consentInfoBo.getUrl(),
              consentInfoBo.getVersion(),
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
      Map<String, String> customIdsMap)
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
              questionnaireBo.getLive(),
              questionnaireBo.getModifiedBy(),
              questionnaireBo.getModifiedDate(),
              questionnaireBo.getRepeatQuestionnaire(),
              questionnaireBo.getScheduleType(),
              questionnaireBo.getShortTitle(),
              questionnaireBo.getStatus(),
              customIdsMap.get(STUDY_ID + questionnaireBo.getStudyId()),
              questionnaireBo.getStudyLifetimeEnd(),
              questionnaireBo.getStudyLifetimeStart(),
              questionnaireBo.getTitle(),
              questionnaireBo.getVersion(),
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
              questionnairesFrequenciesBo.isyDaysSign());
      questionnairesFrequenciesBoInsertQueryList.add(questionnairesFrequenciesBoInsertQuery);
    }

    insertSqlStatements.addAll(questionnairesFrequenciesBoInsertQueryList);
  }

  private String prepareInsertQuery(String sqlQuery, Object... values) throws Exception {
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
                  "<" + column + ">", "'" + values[i].toString().replace("'", "") + "'");
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
    try {
      HttpClient client = HttpClients.createDefault();
      HttpGet httpGet = new HttpGet(signedUrl);
      HttpResponse response = client.execute(httpGet);
      HttpEntity entity = response.getEntity();

      if (entity != null) {
        if (!entity.getContentType().getValue().contains("application/xml")) {
          bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
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
    return validateAndExecuteQuries(signedUrl, map, bufferedReader, sessionObject.getUserId());
  }

  private String validateAndExecuteQuries(
      String signedUrl, Map<String, String> map, BufferedReader bufferedReader, String userId)
      throws Exception {
    try {

      String path = signedUrl.substring(0, signedUrl.indexOf(".sql"));
      String[] tokens = path.split("_");
      long checksum = Long.parseLong(tokens[tokens.length - 1]);
      String version = tokens[tokens.length - 2];

      // validating release version
      ComparableVersion signedUrlVersion = new ComparableVersion(version);
      ComparableVersion releaseVersion = new ComparableVersion(map.get("release.version"));

      if (signedUrlVersion.compareTo(releaseVersion) > 0) {
        throw new Exception(
            IMPORT_FAILED_DUE_TO_INCOMPATIBLE_VERSION + " " + map.get("release.version"));
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
      String StudyId = values[1].substring(values[1].indexOf("'") + 1, values[1].indexOf(",") - 1);
      studyDao.giveStudyPermission(StudyId, userId);

    } catch (Exception e) {
      logger.error("StudyExportService - importStudy() - ERROR ", e);
      if (e instanceof DuplicateKeyException) {
        return IMPORT_FAILED_DUE_TO_ALREADY_USED_URL;
      }
      return e.getMessage();
    }
    return SUCCESS;
  }
}
