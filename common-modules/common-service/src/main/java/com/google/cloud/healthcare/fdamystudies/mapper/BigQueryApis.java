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
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo.SchemaUpdateOption;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.cloud.bigquery.JobStatistics.LoadStatistics;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.common.collect.ImmutableList;
import java.io.File;
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

    // Execute the request and process the results.
    Operation store = request.execute();

    logger.info("FHIR store exported to BigQuery: " + store.toPrettyString());
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
}
