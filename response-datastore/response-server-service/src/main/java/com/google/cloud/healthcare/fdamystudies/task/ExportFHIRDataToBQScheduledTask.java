/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.task;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CONSENT_TABLE_NAME;

import com.google.api.services.healthcare.v1.model.Consent;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Dataset;
import com.google.cloud.bigquery.DatasetId;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.mapper.BigQueryApis;
import com.google.cloud.healthcare.fdamystudies.mapper.ConsentManagementAPIs;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExportFHIRDataToBQScheduledTask {

  private XLogger logger =
      XLoggerFactory.getXLogger(ExportFHIRDataToBQScheduledTask.class.getName());

  @Autowired private BigQueryApis bigQueryApis;

  @Autowired private ApplicationConfiguration appConfig;

  @Autowired ConsentManagementAPIs consentManagementAPIs;

  // 30min fixed delay and 1min initial delay
  @Scheduled(
      fixedDelayString = "${ingest.bigQuery.fixed.delay.ms}",
      initialDelayString = "${ingest.bigQuery.initial.delay.ms}")
  public void exportFHIRDataToBQTask() throws Exception {
    logger.entry("begin exportFHIRConsentDataToBQTask()");
    String WriteDisposition = "WRITE_TRUNCATE";

    try {
      if (appConfig.getEnableBigQuery().equalsIgnoreCase("true")
          && appConfig.getEnableFhirManagementApi().contains("FHIR")
          && !appConfig.getDiscardFhirAfterDid().equalsIgnoreCase("true")) {

        List<com.google.api.services.healthcare.v1.model.Dataset> datasets =
            consentManagementAPIs.datasetList(appConfig.getProjectId(), appConfig.getRegionId());

        if (null != datasets && !datasets.isEmpty()) {
          for (com.google.api.services.healthcare.v1.model.Dataset data : datasets) {

            String FHIRStudyId = data.getName();
            FHIRStudyId = FHIRStudyId.substring(FHIRStudyId.indexOf("/datasets/") + 10);
            logger.info("exportFHIRConsentDataToBQTask() FHIR study ID: " + FHIRStudyId);

            createDataSetInBigQuery("FHIR_" + FHIRStudyId);

            String fhirStoreLocation =
                String.format(
                    "projects/%s/locations/%s/datasets/%s/fhirStores/%s",
                    appConfig.getProjectId(),
                    appConfig.getRegionId(),
                    FHIRStudyId,
                    "FHIR_" + FHIRStudyId);

            bigQueryApis.exportFhirStoreDataToBigQuery(
                fhirStoreLocation, appConfig.getProjectId(), "FHIR_" + FHIRStudyId);
          }
        }
      }
    } catch (Exception e) {
      logger.error("exportFHIRConsentDataToBQTask() FHIR Error ", e);
    }

    try {
      if (appConfig.getEnableBigQuery().equalsIgnoreCase("true")
          && appConfig.getEnableFhirManagementApi().equalsIgnoreCase("FHIR_DID")) {

        List<com.google.api.services.healthcare.v1.model.Dataset> datasets =
            consentManagementAPIs.datasetList(appConfig.getProjectId(), appConfig.getRegionId());

        if (null != datasets && !datasets.isEmpty()) {
          for (com.google.api.services.healthcare.v1.model.Dataset data : datasets) {

            String DIDStudyId = data.getName();
            DIDStudyId = DIDStudyId.substring(DIDStudyId.indexOf("/datasets/") + 10);
            logger.info("exportFHIRConsentDataToBQTask() DID study ID: " + DIDStudyId);

            createDataSetInBigQuery(DIDStudyId);

            String didStoreLocation =
                String.format(
                    "projects/%s/locations/%s/datasets/%s/fhirStores/%s",
                    appConfig.getProjectId(),
                    appConfig.getRegionId(),
                    DIDStudyId,
                    "DID_" + DIDStudyId);

            bigQueryApis.exportFhirStoreDataToBigQuery(
                didStoreLocation, appConfig.getProjectId(), DIDStudyId);
          }
        }
      }
    } catch (Exception e) {
      logger.error("exportFHIRConsentDataToBQTask() DID Error ", e);
    }

    try {
      if (appConfig.getEnableBigQuery().equalsIgnoreCase("true")
          && appConfig.getEnableConsentManagementAPI().equalsIgnoreCase("true")) {

        List<com.google.api.services.healthcare.v1.model.Dataset> datasets =
            consentManagementAPIs.datasetList(appConfig.getProjectId(), appConfig.getRegionId());

        if (null != datasets && !datasets.isEmpty()) {
          for (com.google.api.services.healthcare.v1.model.Dataset data : datasets) {

            String ConsentStudyId = data.getName();
            ConsentStudyId = ConsentStudyId.substring(ConsentStudyId.indexOf("/datasets/") + 10);
            logger.info("exportFHIRConsentDataToBQTask() Consent study ID: " + ConsentStudyId);

            createDataSetInBigQuery(ConsentStudyId);

            // Ingest consent data
            List<Object> consentDataBQ = new ArrayList<Object>();

            String consentDatasetName =
                String.format(
                    "projects/%s/locations/%s/datasets/%s/consentStores/%s",
                    appConfig.getProjectId(),
                    appConfig.getRegionId(),
                    ConsentStudyId,
                    "CONSENT_" + ConsentStudyId);

            List<Consent> consentDataList =
                consentManagementAPIs.getListOfConsents("", consentDatasetName);

            if (null != consentDataList && !consentDataList.isEmpty()) {
              for (Consent consentList : consentDataList) {

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("ParticipantId", consentList.getUserId());
                jsonObj.put("ConsentState", consentList.getState());
                jsonObj.put("ConsentType", consentList.getMetadata().get("ConsentType"));
                jsonObj.put(
                    "DataSharingPermission",
                    consentList.getMetadata().get("DataSharingPermission"));

                consentDataBQ.add(jsonObj);
              }
            }

            if (null != consentDataBQ && !consentDataBQ.isEmpty()) {
              bigQueryApis.ingestDataToBigQueryTable(
                  appConfig.getProjectId(),
                  appConfig.getRegionId(),
                  ConsentStudyId,
                  CONSENT_TABLE_NAME,
                  consentDataBQ,
                  WriteDisposition);
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("exportFHIRConsentDataToBQTask() Consent Error ", e);
    }

    logger.exit("exportFHIRConsentDataToBQTask() completed");
  }

  public void createDataSetInBigQuery(String dataSetName) {

    logger.entry("ExportFHIRDataToBQScheduledTask.createDataSetInBigQuery() entry");
    try {
      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      BigQuery bigquery =
          BigQueryOptions.newBuilder()
              .setProjectId(appConfig.getProjectId())
              .setLocation(appConfig.getRegionId())
              .build()
              .getService();
      // BigQuery bigquery = gigQueryOptions.getDefaultInstance().getService();

      Dataset dataset = bigquery.getDataset(DatasetId.of(dataSetName));

      logger.info("FHIR dataSetName: " + dataSetName);
      if (dataset == null) {
        DatasetInfo datasetInfo =
            DatasetInfo.newBuilder(dataSetName).setLocation(appConfig.getRegionId()).build();

        Dataset newDataset = bigquery.create(datasetInfo);
        String newDatasetName = newDataset.getDatasetId().getDataset();
        logger.info("New Dataset created in BigQuery: " + newDatasetName);
      }

    } catch (BigQueryException e) {
      logger.error("ExportFHIRDataToBQScheduledTask.createDataSetInBigQuery() error ", e);
    }
    logger.exit("ExportFHIRDataToBQScheduledTask.createDataSetInBigQuery() completed");
  }
}
