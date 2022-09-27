/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.api.client.util.Charsets;
import com.google.api.services.healthcare.v1.CloudHealthcare;
import com.google.api.services.healthcare.v1.CloudHealthcare.Projects.Locations.Datasets.FhirStores;
import com.google.api.services.healthcare.v1.model.ExportResourcesRequest;
import com.google.api.services.healthcare.v1.model.GoogleCloudHealthcareV1FhirBigQueryDestination;
import com.google.api.services.healthcare.v1.model.Operation;
import com.google.api.services.healthcare.v1.model.SchemaConfig;
import com.google.cloud.RetryOption;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo.SchemaUpdateOption;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.cloud.bigquery.JobStatistics.LoadStatistics;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.ViewDefinition;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.stereotype.Component;
import org.threeten.bp.Duration;

@Component
public class BigQueryApis {
  private static XLogger logger = XLoggerFactory.getXLogger(BigQueryApis.class.getName());

  public void exportFhirStoreDataToBigQuery(
      String fhirStoreName, String projectId, String datasetId) throws Exception {

    logger.entry("begin BigQueryApis.exportFhirStoreDataToBigQuery()");
    // Execute the request and process the results.
    Operation store;
    try {

      // Initialize the client, which will be used to interact with the service.
      CloudHealthcare client = ConsentManagementAPIs.createClient();
      GoogleCloudHealthcareV1FhirBigQueryDestination bigqueryDestination =
          new GoogleCloudHealthcareV1FhirBigQueryDestination();

      SchemaConfig schemaConfig = new SchemaConfig();
      ExportResourcesRequest exportResourcesRequest = new ExportResourcesRequest();
      long recursiveStructureDepth = 5;
      exportResourcesRequest.setBigqueryDestination(
          bigqueryDestination
              .setDatasetUri("bq://" + projectId + "." + datasetId)
              .setForce(true)
              .setWriteDisposition("WRITE_TRUNCATE")
              .setSchemaConfig(
                  schemaConfig
                      .setSchemaType("ANALYTICS")
                      .setRecursiveStructureDepth(recursiveStructureDepth)));

      // Create request and configure any parameters.
      FhirStores.Export request =
          client
              .projects()
              .locations()
              .datasets()
              .fhirStores()
              .export(fhirStoreName, exportResourcesRequest);

      store = request.execute();
      logger.debug("FHIR store exported to BigQuery: " + store.toPrettyString());
    } catch (IOException e) {
      logger.error("BigQueryApis.exportFhirStoreDataToBigQuery() - error ", e);
    }

    logger.exit("BigQueryApis.exportFhirStoreDataToBigQuery() - Ends ");
  }

  public void ingestDataToBigQueryTable(
      String bqProjectId,
      String bqLocation,
      String bqDatasetName,
      String tableName,
      List<?> dataList,
      String writeDisposition)
      throws IOException, InterruptedException, TimeoutException {

    logger.entry("begin BigQueryApis.ingestDataToBigQueryTable()");
    LoadStatistics stats = null;
    TableDataWriteChannel writer = null;
    StringBuilder formattedData = new StringBuilder();
    WriteChannelConfiguration writeChannelConfiguration;
    try {

      BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
      // [START bigquery_load_from_file]
      TableId tableId = TableId.of(bqProjectId, bqDatasetName, tableName);

      if (writeDisposition.equalsIgnoreCase("WRITE_TRUNCATE")) {
        writeChannelConfiguration =
            WriteChannelConfiguration.newBuilder(tableId)
                .setFormatOptions(FormatOptions.json())
                .setAutodetect(true)
                .setWriteDisposition(WriteDisposition.WRITE_TRUNCATE)
                .build();
      } else {
        writeChannelConfiguration =
            WriteChannelConfiguration.newBuilder(tableId)
                .setFormatOptions(FormatOptions.json())
                .setAutodetect(true)
                .setWriteDisposition(WriteDisposition.WRITE_APPEND)
                .setSchemaUpdateOptions(ImmutableList.of(SchemaUpdateOption.ALLOW_FIELD_ADDITION))
                .build();
      }
      // The location must be specified; other fields can be auto-detected.
      JobId jobId = JobId.newBuilder().setLocation(bqLocation).build();
      writer = bigQuery.writer(jobId, writeChannelConfiguration);

      for (Object jsonData : dataList) {

        String jso = jsonData.toString();
        jso = jso.replaceAll("\\s", "");
        formattedData.append(jso).append(System.getProperty("line.separator"));
      }

      try {
        writer.write(ByteBuffer.wrap((formattedData.toString()).getBytes(Charsets.UTF_8)));
      } finally {
        writer.close();
      }

      // Get load job
      Job job = writer.getJob();
      job =
          job.waitFor(
              RetryOption.initialRetryDelay(Duration.ofSeconds(1)),
              RetryOption.totalTimeout(Duration.ofMinutes(3)));
      stats = job.getStatistics();
      logger.info("BigQuery ingest State: " + job.getStatus().getState());
      logger.info("BigQuery number of rows updated: " + stats.getOutputRows());

      // [END bigquery_load_from_file]
    } catch (Exception e) {
      logger.error("%s BigQueryApis.ingestDataToBigQueryTable() - error " + e);
    }

    logger.exit("BigQueryApis.ingestDataToBigQueryTable() - Ends ");
  }

  public void createViewsInBigQuery(String projectId, String datasetName) throws Exception {

    logger.entry("GoogleBigQuery.createViewsInBigQuery() Begins ");
    String questionnaireTable = "Questionnaire";
    String questionnaireResponseTable = "QuestionnaireResponse";
    String patientTable = "Patient";
    String consentDataTable = "consent_data";
    String ViewQuestionnaireForSpecificStudy = "ViewQuestionnaireForSpecificStudy";
    String ViewAllParticipantResponsesForSpecificStudy =
        "ViewAllParticipantResponsesForSpecificStudy";
    String ViewAllParticipantResponsesForSpecificSiteId =
        "ViewAllParticipantResponsesForSpecificSiteId";
    String viewAllParticipantResponsesForSpecificParticipantId =
        "ViewAllParticipantResponsesForSpecificParticipantId";

    String query0 =
        String.format(
            "SELECT\r\n"
                + "  questionnaire.id,\r\n"
                + "  questionnaire.name,\r\n"
                + "  questionnaire.status,\r\n"
                + "  questionnaire.title,\r\n"
                + "  questionnaire.version,\r\n"
                + "  questionnaire.date,\r\n"
                + "  questionnaire.effectivePeriod.start AS effectiveStartPeriod,\r\n"
                + "  questionnaire.effectivePeriod.end AS effectiveEndPeriod,\r\n"
                + "  \r\n"
                + "  --questionnaire.Schedule.Anchor_Date.value.string AS Anchor_Date,\r\n"
                + "  questionnaire.Schedule.Schedule_Option.value.string AS Schedule_Option,\r\n"
                + "  questionnaire.Schedule.Schedule_Type.value.string AS Schedule_Type,\r\n"
                + "\r\n"
                + "  questionnaire.StudyMetaData.StudyID.value.string AS StudyID,\r\n"
                + "  questionnaire.StudyMetaData.StudyName.value.string AS StudyName,\r\n"
                + "  questionnaire.StudyMetaData.StudyVersion.value.string AS StudyVersion,\r\n"
                + "\r\n"
                + "  identifier.type.text AS identifier_type,\r\n"
                + "  identifier.use AS identifier_use,\r\n"
                + "  identifier.value AS identifier_value,\r\n"
                + "\r\n"
                + "  items.linkId AS items_linkId,\r\n"
                + "  items.definition AS item_definition,\r\n"
                + "  items.repeats AS items_repeats,\r\n"
                + "  items.required AS items_required,\r\n"
                + "  items.text AS items_text,\r\n"
                + "  items.type AS items_type,\r\n"
                + "  -- Uncomment below columns \r\n"
                + "  --items.DefaultSliderValue.value.integer AS DefaultSliderValue,\r\n"
                + "  --items.DefaultSliderValue.value.string AS DefaultSliderValue,\r\n"
                + "  --items.Description_for_maximum_value.value.string AS Description_for_maximum_value,\r\n"
                + "  --items.Description_for_minimum_value.value.string AS Description_for_minimum_value,\r\n"
                + "  --items.maxValue.value.integer AS maxValue,\r\n"
                + "  --items.minValue.value.integer AS minValue,\r\n"
                + "  --items.questionnaire_sliderStepValue.value.integer AS questionnaire_sliderStepValue,\r\n"
                + "  --items.regex.value.string AS regex,\r\n"
                + "\r\n"
                + "  items.enableBehavior,\r\n"
                + "  enableWhen.answer.string AS enableWhen_answer,\r\n"
                + "  enableWhen.operator AS enableWhen_operator,\r\n"
                + "  enableWhen.question AS enableWhen_question,\r\n"
                + "  answerOptions.value.string AS answerOptions,\r\n"
                + "  initial.value.string AS initial_value,\r\n"
                + "\r\n"
                + "  items2.linkId AS items_linkId2,\r\n"
                + "  items2.definition AS item_definition2,\r\n"
                + "  items2.repeats AS items_repeats2,\r\n"
                + "  items2.required AS items_required2,\r\n"
                + "  items2.text AS items_text2,\r\n"
                + "  items2.type AS items_type2,\r\n"
                + "  answerOptions2.value.string AS answerOptions_value2\r\n"
                + "\r\n"
                + "FROM\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + questionnaireTable
                + "` AS questionnaire\r\n"
                + "  CROSS JOIN UNNEST(questionnaire.identifier) AS identifier\r\n"
                + "  CROSS JOIN UNNEST(questionnaire.item) AS items\r\n"
                + "  LEFT JOIN UNNEST(items.answerOption) AS answerOptions\r\n"
                + "  LEFT JOIN UNNEST(items.enableWhen) AS enableWhen\r\n"
                + "  LEFT JOIN UNNEST(items.initial) AS initial\r\n"
                + "  LEFT JOIN UNNEST(items.item) AS items2\r\n"
                + "  LEFT JOIN UNNEST(items2.answerOption) AS answerOptions2\r\n"
                + "\r\n"
                + "-- WHERE\r\n"
                + "  -- questionnaire.name IN ('update_activity_id_here') \r\n"
                + "  \r\n"
                + "ORDER BY questionnaire.date DESC;");

    createViewInBigQuery(projectId, datasetName, ViewQuestionnaireForSpecificStudy, query0);

    String query1 =
        String.format(
            "SELECT\r\n"
                + "  questionnaireResponse.id,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(1)] AS studyID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(2)] AS SiteID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(3)] AS ParticipantID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(4)] AS ActivityID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(5)] AS RunID,\r\n"
                + "  questionnaireResponse.authored AS responseCreated, --Date the answers were gathered\r\n"
                + "  questionnaireResponse.identifier.type.text AS TaskType,\r\n"
                + "  questionnaireResponse.identifier.use,\r\n"
                + "\r\n"
                + "  consentData.ConsentType  as ConsentType,\r\n"
                + "  consentData.ConsentState  as ConsentState,\r\n"
                + "  consentData.DataSharingPermission,\r\n"
                + "  -- Participant DATA \r\n"
                + "  patient.active AS ParticipantEnrollmentStatus,\r\n"
                + "  patient.meta.lastUpdated AS ParticipantDataUpdatedTime,\r\n"
                + "  questionnaire AS questionnaire_fhir_path,   -- \r\n"
                + "  status AS response_status, --\r\n"
                + "\r\n"
                + "  questionnaireResponse.source.patientId as PatientId,\r\n"
                + "  questionnaireResponse.source.type as QuestionnaireSourceType,\r\n"
                + "  questionnaireResponse.status as QuestionnaireResponseStatus,\r\n"
                + "  item1.definition AS QuestionnaireDefinition , -- Provides the details for the item \r\n"
                + "  item1.linkId AS questionnaire_linkID, -- Pointer to specific item from Questionnaire \r\n"
                + "  item1.text AS Questionnaire_question_text, -- Text of the question being answered \r\n"
                + "\r\n"
                + "  itemAns1.value.uri AS QuestionnaireUriAnswer,\r\n"
                + "  itemAns1.value.boolean AS QuestionnaireBooleanAnswer,\r\n"
                + "  itemAns1.value.dateTime AS QuestionnaireDateTimeAnswer,\r\n"
                + "  itemAns1.value.decimal AS QuestionnaireDecimalAnswer,\r\n"
                + "  itemAns1.value.integer AS QuestionnaireIntegerAnswer,\r\n"
                + "  itemAns1.value.string AS QuestionnaireStringAnswer,\r\n"
                + "  itemAns1.value.time AS QuestionnaireAnswerTime,\r\n"
                + "\r\n"
                + "  item2.definition AS QuestionnaireDefinition , -- Provides the details for the item \r\n"
                + "  item2.linkId AS questionnaire_linkID, -- Pointer to specific item from Questionnaire \r\n"
                + "  item2.text AS Questionnaire_question_text, -- Text of the question being answered \r\n"
                + "\r\n"
                + "  itemAns2.value.uri AS QuestionnaireUriAnswer,\r\n"
                + "  itemAns2.value.boolean AS QuestionnaireBooleanAnswer,\r\n"
                + "  itemAns2.value.dateTime AS QuestionnaireDateTimeAnswer,\r\n"
                + "  itemAns2.value.decimal AS QuestionnaireDecimalAnswer,\r\n"
                + "  itemAns2.value.integer AS QuestionnaireIntegerAnswer,\r\n"
                + "  itemAns2.value.string AS QuestionnaireStringAnswer,\r\n"
                + "  itemAns2.value.time AS QuestionnaireAnswerTime,\r\n"
                + "\r\n"
                + "  item3.definition AS ActiveTaskDefinition , -- Provides the details for the item\r\n"
                + "  item3.linkId AS ActiveTask_linkID, -- Pointer to specific item from Questionnaire \r\n"
                + "  item3.text AS ActiveTask_question_text, -- Text of the question being answered   \r\n"
                + "\r\n"
                + "  itemAns3.value.uri AS ActiveTaskUriAnswer,\r\n"
                + "  itemAns3.value.boolean AS ActiveTaskBooleanAnswer,\r\n"
                + "  itemAns3.value.dateTime AS ActiveTaskDateTimeAnswer,\r\n"
                + "  itemAns3.value.decimal AS ActiveTaskDecimalAnswer,\r\n"
                + "  itemAns3.value.integer AS ActiveTaskIntegerAnswer,\r\n"
                + "  itemAns3.value.string AS ActiveTaskStringAnswer,\r\n"
                + "  itemAns3.value.time AS ActiveTaskAnswerTime\r\n"
                + "\r\n"
                + "FROM\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + questionnaireResponseTable
                + "` AS questionnaireResponse\r\n"
                + "  LEFT JOIN UNNEST(questionnaireResponse.item) AS item1\r\n"
                + "  LEFT JOIN UNNEST(item1.answer) AS itemAns1\r\n"
                + "  LEFT JOIN UNNEST(item1.item) AS item2\r\n"
                + "  LEFT JOIN UNNEST(item2.answer) AS itemAns2\r\n"
                + "  LEFT JOIN UNNEST(itemAns1.item) AS item3  \r\n"
                + "  LEFT JOIN UNNEST(item3.answer) AS itemAns3 \r\n"
                + "LEFT OUTER JOIN\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + patientTable
                + "` AS patient\r\n"
                + "ON\r\n"
                + "  SPLIT(source.patientId, '/')[safe_ORDINAL(1)] = patient.id\r\n"
                + "  CROSS JOIN UNNEST(patient.identifier) AS participantId\r\n"
                + "LEFT OUTER JOIN\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + consentDataTable
                + "` AS consentData\r\n"
                + "ON\r\n"
                + "  participantId.value = consentData.ParticipantId \r\n"
                + "WHERE\r\n"
                + "  consentData.ConsentState NOT IN ('REVOKED', 'REJECTED') \r\n"
                + "ORDER BY authored DESC;");

    createViewInBigQuery(
        projectId, datasetName, ViewAllParticipantResponsesForSpecificStudy, query1);

    String query2 =
        String.format(
            "SELECT\r\n"
                + "  questionnaireResponse.id,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(1)] AS studyID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(2)] AS SiteID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(3)] AS ParticipantID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(4)] AS ActivityID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(5)] AS RunID,\r\n"
                + "  questionnaireResponse.authored AS responseCreated, --Date the answers were gathered\r\n"
                + "  questionnaireResponse.identifier.type.text AS TaskType,\r\n"
                + "  questionnaireResponse.identifier.use,\r\n"
                + "\r\n"
                + "  consentData.ConsentType  as ConsentType,\r\n"
                + "  consentData.ConsentState  as ConsentState,\r\n"
                + "  consentData.DataSharingPermission,\r\n"
                + "  -- Participant DATA \r\n"
                + "  patient.active AS ParticipantEnrollmentStatus,\r\n"
                + "  patient.meta.lastUpdated AS ParticipantDataUpdatedTime,\r\n"
                + "  questionnaire AS questionnaire_fhir_path,   -- \r\n"
                + "  status AS response_status, --\r\n"
                + "\r\n"
                + "  questionnaireResponse.source.patientId as PatientId,\r\n"
                + "  questionnaireResponse.source.type as QuestionnaireSourceType,\r\n"
                + "  questionnaireResponse.status as QuestionnaireResponseStatus,\r\n"
                + "  item1.definition AS QuestionnaireDefinition , -- Provides the details for the item \r\n"
                + "  item1.linkId AS questionnaire_linkID, -- Pointer to specific item from Questionnaire \r\n"
                + "  item1.text AS Questionnaire_question_text, -- Text of the question being answered \r\n"
                + "\r\n"
                + "  itemAns1.value.uri AS QuestionnaireUriAnswer,\r\n"
                + "  itemAns1.value.boolean AS QuestionnaireBooleanAnswer,\r\n"
                + "  itemAns1.value.dateTime AS QuestionnaireDateTimeAnswer,\r\n"
                + "  itemAns1.value.decimal AS QuestionnaireDecimalAnswer,\r\n"
                + "  itemAns1.value.integer AS QuestionnaireIntegerAnswer,\r\n"
                + "  itemAns1.value.string AS QuestionnaireStringAnswer,\r\n"
                + "  itemAns1.value.time AS QuestionnaireAnswerTime,\r\n"
                + "\r\n"
                + "  item2.definition AS QuestionnaireDefinition , -- Provides the details for the item \r\n"
                + "  item2.linkId AS questionnaire_linkID, -- Pointer to specific item from Questionnaire \r\n"
                + "  item2.text AS Questionnaire_question_text, -- Text of the question being answered \r\n"
                + "\r\n"
                + "  itemAns2.value.uri AS QuestionnaireUriAnswer,\r\n"
                + "  itemAns2.value.boolean AS QuestionnaireBooleanAnswer,\r\n"
                + "  itemAns2.value.dateTime AS QuestionnaireDateTimeAnswer,\r\n"
                + "  itemAns2.value.decimal AS QuestionnaireDecimalAnswer,\r\n"
                + "  itemAns2.value.integer AS QuestionnaireIntegerAnswer,\r\n"
                + "  itemAns2.value.string AS QuestionnaireStringAnswer,\r\n"
                + "  itemAns2.value.time AS QuestionnaireAnswerTime,\r\n"
                + "\r\n"
                + "  item3.definition AS ActiveTaskDefinition , -- Provides the details for the item\r\n"
                + "  item3.linkId AS ActiveTask_linkID, -- Pointer to specific item from Questionnaire \r\n"
                + "  item3.text AS ActiveTask_question_text, -- Text of the question being answered   \r\n"
                + "\r\n"
                + "  itemAns3.value.uri AS ActiveTaskUriAnswer,\r\n"
                + "  itemAns3.value.boolean AS ActiveTaskBooleanAnswer,\r\n"
                + "  itemAns3.value.dateTime AS ActiveTaskDateTimeAnswer,\r\n"
                + "  itemAns3.value.decimal AS ActiveTaskDecimalAnswer,\r\n"
                + "  itemAns3.value.integer AS ActiveTaskIntegerAnswer,\r\n"
                + "  itemAns3.value.string AS ActiveTaskStringAnswer,\r\n"
                + "  itemAns3.value.time AS ActiveTaskAnswerTime\r\n"
                + "\r\n"
                + "FROM\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + questionnaireResponseTable
                + "` AS questionnaireResponse\r\n"
                + "  LEFT JOIN UNNEST(questionnaireResponse.item) AS item1\r\n"
                + "  LEFT JOIN UNNEST(item1.answer) AS itemAns1\r\n"
                + "  LEFT JOIN UNNEST(item1.item) AS item2\r\n"
                + "  LEFT JOIN UNNEST(item2.answer) AS itemAns2\r\n"
                + "  LEFT JOIN UNNEST(itemAns1.item) AS item3  \r\n"
                + "  LEFT JOIN UNNEST(item3.answer) AS itemAns3 \r\n"
                + "LEFT OUTER JOIN\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + patientTable
                + "` AS patient\r\n"
                + "ON\r\n"
                + "  SPLIT(source.patientId, '/')[safe_ORDINAL(1)] = patient.id\r\n"
                + "  CROSS JOIN UNNEST(patient.identifier) AS participantId\r\n"
                + "LEFT OUTER JOIN\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + consentDataTable
                + "` AS consentData\r\n"
                + "ON\r\n"
                + "  participantId.value = consentData.ParticipantId \r\n"
                + "WHERE\r\n"
                + "  consentData.ConsentState NOT IN ('REVOKED', 'REJECTED') \r\n"
                + "  -- AND SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(2)] IN ('Update-Site-ID-Here') \r\n"
                + "  -- AND SPLIT(questionnaireResponse.identifier.value, '@')[safe_ordinal(4)]  IN ('Upate-Activity-ID-Here') \r\n"
                + "ORDER BY authored DESC\r\n"
                + " -- LIMIT 1000");

    createViewInBigQuery(
        projectId, datasetName, ViewAllParticipantResponsesForSpecificSiteId, query2);

    String query3 =
        String.format(
            "SELECT\r\n"
                + "  questionnaireResponse.id,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(1)] AS studyID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(2)] AS SiteID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(3)] AS ParticipantID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(4)] AS ActivityID,\r\n"
                + "  SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(5)] AS RunID,\r\n"
                + "  questionnaireResponse.authored AS responseCreated, --Date the answers were gathered\r\n"
                + "  questionnaireResponse.identifier.type.text AS TaskType,\r\n"
                + "  questionnaireResponse.identifier.use,\r\n"
                + "\r\n"
                + "  consentData.ConsentType  as ConsentType,\r\n"
                + "  consentData.ConsentState  as ConsentState,\r\n"
                + "  consentData.DataSharingPermission,\r\n"
                + "  -- Participant DATA \r\n"
                + "  patient.active AS ParticipantEnrollmentStatus,\r\n"
                + "  patient.meta.lastUpdated AS ParticipantDataUpdatedTime,\r\n"
                + "  questionnaire AS questionnaire_fhir_path,   -- \r\n"
                + "  status AS response_status, --\r\n"
                + "\r\n"
                + "  questionnaireResponse.source.patientId as PatientId,\r\n"
                + "  questionnaireResponse.source.type as QuestionnaireSourceType,\r\n"
                + "  questionnaireResponse.status as QuestionnaireResponseStatus,\r\n"
                + "  item1.definition AS QuestionnaireDefinition , -- Provides the details for the item \r\n"
                + "  item1.linkId AS questionnaire_linkID, -- Pointer to specific item from Questionnaire \r\n"
                + "  item1.text AS Questionnaire_question_text, -- Text of the question being answered \r\n"
                + "\r\n"
                + "  itemAns1.value.uri AS QuestionnaireUriAnswer,\r\n"
                + "  itemAns1.value.boolean AS QuestionnaireBooleanAnswer,\r\n"
                + "  itemAns1.value.dateTime AS QuestionnaireDateTimeAnswer,\r\n"
                + "  itemAns1.value.decimal AS QuestionnaireDecimalAnswer,\r\n"
                + "  itemAns1.value.integer AS QuestionnaireIntegerAnswer,\r\n"
                + "  itemAns1.value.string AS QuestionnaireStringAnswer,\r\n"
                + "  itemAns1.value.time AS QuestionnaireAnswerTime,\r\n"
                + "\r\n"
                + "  item2.definition AS QuestionnaireDefinition , -- Provides the details for the item \r\n"
                + "  item2.linkId AS questionnaire_linkID, -- Pointer to specific item from Questionnaire \r\n"
                + "  item2.text AS Questionnaire_question_text, -- Text of the question being answered \r\n"
                + "\r\n"
                + "  itemAns2.value.uri AS QuestionnaireUriAnswer,\r\n"
                + "  itemAns2.value.boolean AS QuestionnaireBooleanAnswer,\r\n"
                + "  itemAns2.value.dateTime AS QuestionnaireDateTimeAnswer,\r\n"
                + "  itemAns2.value.decimal AS QuestionnaireDecimalAnswer,\r\n"
                + "  itemAns2.value.integer AS QuestionnaireIntegerAnswer,\r\n"
                + "  itemAns2.value.string AS QuestionnaireStringAnswer,\r\n"
                + "  itemAns2.value.time AS QuestionnaireAnswerTime,\r\n"
                + "\r\n"
                + "  item3.definition AS ActiveTaskDefinition , -- Provides the details for the item\r\n"
                + "  item3.linkId AS ActiveTask_linkID, -- Pointer to specific item from Questionnaire \r\n"
                + "  item3.text AS ActiveTask_question_text, -- Text of the question being answered   \r\n"
                + "\r\n"
                + "  itemAns3.value.uri AS ActiveTaskUriAnswer,\r\n"
                + "  itemAns3.value.boolean AS ActiveTaskBooleanAnswer,\r\n"
                + "  itemAns3.value.dateTime AS ActiveTaskDateTimeAnswer,\r\n"
                + "  itemAns3.value.decimal AS ActiveTaskDecimalAnswer,\r\n"
                + "  itemAns3.value.integer AS ActiveTaskIntegerAnswer,\r\n"
                + "  itemAns3.value.string AS ActiveTaskStringAnswer,\r\n"
                + "  itemAns3.value.time AS ActiveTaskAnswerTime\r\n"
                + "\r\n"
                + "FROM\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + questionnaireResponseTable
                + "` AS questionnaireResponse\r\n"
                + "  LEFT JOIN UNNEST(questionnaireResponse.item) AS item1\r\n"
                + "  LEFT JOIN UNNEST(item1.answer) AS itemAns1\r\n"
                + "  LEFT JOIN UNNEST(item1.item) AS item2\r\n"
                + "  LEFT JOIN UNNEST(item2.answer) AS itemAns2\r\n"
                + "  LEFT JOIN UNNEST(itemAns1.item) AS item3  \r\n"
                + "  LEFT JOIN UNNEST(item3.answer) AS itemAns3 \r\n"
                + "LEFT OUTER JOIN\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + patientTable
                + "` AS patient\r\n"
                + "ON\r\n"
                + "  SPLIT(source.patientId, '/')[safe_ORDINAL(1)] = patient.id\r\n"
                + "  CROSS JOIN UNNEST(patient.identifier) AS participantId\r\n"
                + "LEFT OUTER JOIN\r\n"
                + "  `"
                + projectId
                + "."
                + datasetName
                + "."
                + consentDataTable
                + "` AS consentData\r\n"
                + "ON\r\n"
                + "  participantId.value = consentData.ParticipantId \r\n"
                + "WHERE\r\n"
                + "  consentData.ConsentState NOT IN ('REVOKED', 'REJECTED') \r\n"
                + "  -- AND SPLIT(questionnaireResponse.identifier.value, '@')[safe_ORDINAL(3)] IN ('Update-Participant-ID-Here') \r\n"
                + "  -- AND SPLIT(questionnaireResponse.identifier.value, '@')[safe_ordinal(4)] IN ('Update-Activity-ID-Here') \r\n"
                + "  -- AND SPLIT(questionnaireResponse.identifier.value, '@')[safe_ordinal(5)] IN ('Update-Run-ID-Here') \r\n"
                + "ORDER BY authored DESC\r\n"
                + " -- LIMIT 1000");

    createViewInBigQuery(
        projectId, datasetName, viewAllParticipantResponsesForSpecificParticipantId, query3);

    logger.exit("GoogleBigQuery.createViewsInBigQuery() Ends ");
  }

  public static void createViewInBigQuery(
      String bqProjectId, String datasetName, String viewName, String query) {
    try {
      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

      TableId tableId = TableId.of(bqProjectId, datasetName, viewName);

      ViewDefinition viewDefinition =
          ViewDefinition.newBuilder(query).setUseLegacySql(false).build();

      bigquery.create(TableInfo.of(tableId, viewDefinition));
      logger.debug("View created successfully");
    } catch (BigQueryException e) {
      logger.error("View was not created. \n" + e.toString());
    }
  }
}
