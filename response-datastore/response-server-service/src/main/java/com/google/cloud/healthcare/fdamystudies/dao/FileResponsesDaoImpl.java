/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("fileResponsesDaoImpl")
public class FileResponsesDaoImpl implements ResponsesDao {
  @Autowired private ApplicationConfiguration appConfig;

  private XLogger logger = XLoggerFactory.getXLogger(FileResponsesDaoImpl.class.getName());

  @Override
  public void saveStudyMetadata(
      String studyCollectionName, String studyId, Map<String, Object> dataToStore)
      throws ProcessResponseException {
    if (studyCollectionName != null && dataToStore != null) {
      try {

        logger.info("saveStudyMetadata() : \n Study Collection Name: " + studyCollectionName);
        // Implement save of responses data
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String studyMetadataJsonStr = gson.toJson(dataToStore);
        String studyDirName = appConfig.getResponseDataFilePath() + studyId;
        Path studyDirPath = Paths.get(studyDirName);
        if (!Files.exists(studyDirPath)) {
          Files.createDirectories(studyDirPath);
        }

        Files.write(
            Paths.get(
                studyDirPath
                    + AppConstants.FILE_SEPARATOR
                    + studyCollectionName
                    + AppConstants.JSON_FILE_EXTENSION),
            studyMetadataJsonStr.getBytes(),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
        logger.debug(
            "saveStudyMetadata() : \n Study Collection Name: "
                + studyCollectionName
                + " save successfully. Path to StudyMetadata file: "
                + appConfig.getResponseDataFilePath()
                + studyCollectionName
                + studyDirPath
                + AppConstants.FILE_SEPARATOR
                + studyCollectionName
                + AppConstants.JSON_FILE_EXTENSION);

      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        throw new ProcessResponseException(e.getMessage());
      }
    }
    {
      throw new ProcessResponseException(
          "FileResponsesDaoImpl.saveStudyMetadata() - "
              + "Study Collection is null or dataToStore is null");
    }
  }

  @Override
  public StoredResponseBean getActivityResponseDataForParticipant(
      String studyCollectionName,
      String studyId,
      String siteId,
      String participantId,
      String activityId,
      String questionKey)
      throws ProcessResponseException {
    // Unimplemeted method. A reference implementation has been provided for Cloud Firestore
    throw new ProcessResponseException(
        "Unimplemented method. Needs to be implemented with a datastore");
  }

  @Override
  public void saveActivityResponseData(
      String studyCollectionName,
      String studyId,
      String activitiesCollectionName,
      Map<String, Object> dataToStoreActivityResults)
      throws ProcessResponseException {
    // The conversion of the response data to a file is provided as sample implementation.
    // Implementors should use the file representation of the response object or the
    // JSON representation of the response object to store the responses in a suitable data store
    if (studyCollectionName != null && dataToStoreActivityResults != null) {
      try {
        logger.info(
            "saveActivityResponseData() : \n Study Collection Name: " + studyCollectionName);
        Gson gson = new Gson();
        StringBuilder studyResponseDataJsonStr = new StringBuilder();
        studyResponseDataJsonStr.append(gson.toJson(dataToStoreActivityResults));

        String studyDirName = appConfig.getResponseDataFilePath() + studyId;
        Path studyDirPath = Paths.get(studyDirName);
        if (!Files.exists(studyDirPath)) {
          Files.createDirectories(studyDirPath);
        }
        Files.write(
            Paths.get(
                studyDirPath
                    + AppConstants.FILE_SEPARATOR
                    + studyCollectionName
                    + AppConstants.HYPHEN
                    + System.currentTimeMillis()
                    + AppConstants.JSON_FILE_EXTENSION),
            studyResponseDataJsonStr.toString().getBytes(),
            StandardOpenOption.CREATE);
        logger.debug(
            "saveActivityResponseData() : \n Document in study collection: "
                + studyCollectionName
                + " with document ID. Path to StudyMetadata file: \n"
                + studyDirPath
                + AppConstants.FILE_SEPARATOR
                + studyCollectionName
                + AppConstants.HYPHEN
                + System.currentTimeMillis()
                + AppConstants.JSON_FILE_EXTENSION);
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        throw new ProcessResponseException(
            "FileResponsesDaoImpl.saveActivityResponseData() - "
                + "Exception when saving data to file storage: "
                + e.getMessage());
      }
    } else {
      throw new ProcessResponseException(
          "FileResponsesDaoImpl.saveActivityResponseData() - "
              + "Study Collection is null or dataToStoreResults is null");
    }
  }

  @Override
  public void deleteActivityResponseDataForParticipant(
      String studyCollectionName,
      String studyId,
      String activitiesCollectionName,
      String participantId)
      throws ProcessResponseException {
    // Unimplemeted method. A reference implementation has been provided for Cloud Firestore
    // Implementation of the query to return response data, goes here
    throw new ProcessResponseException(
        "Unimplemented method. Needs to be implemented with a datastore");
  }

  @Override
  public void updateWithdrawalStatusForParticipant(
      String studyCollectionName, String studyId, String participantId)
      throws ProcessResponseException {
    // Unimplemeted method. A reference implementation has been provided for Cloud Firestore
    throw new ProcessResponseException(
        "Unimplemented method. Needs to be implemented with a datastore");
  }
}
