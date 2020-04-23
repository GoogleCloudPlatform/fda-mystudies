/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityMetaDataBean;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyActivityMetadataRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.ResponsesDao;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;

@Service
public class StudyMetadataServiceImpl implements StudyMetadataService {
  @Autowired private RestTemplate restTemplate;
  @Autowired private ApplicationConfiguration appConfig;

  @Autowired
  @Qualifier("cloudFirestoreResponsesDaoImpl")
  private ResponsesDao responsesDao;

  private static Logger logger = LoggerFactory.getLogger(StudyMetadataServiceImpl.class);

  @Override
  public void saveStudyMetadata(StudyMetadataBean studyMetadataBean)
      throws ProcessResponseException {

    try {
      BeanInfo beanInfo;
      beanInfo = Introspector.getBeanInfo(studyMetadataBean.getClass());
      PropertyDescriptor[] propDescriptor = beanInfo.getPropertyDescriptors();
      Map<String, Object> dataToStore = new HashMap<>();
      for (PropertyDescriptor pd : propDescriptor) {
        String propertyName = pd.getName();
        if (!propertyName.equals("class")) {
          Method getterMethod = pd.getReadMethod();
          try {
            Object propertyValue = getterMethod.invoke(studyMetadataBean);
            if (propertyValue == null) {
              propertyValue = AppConstants.EMPTY_STR;
            }
            logger.debug(
                "saveStudyMetadata() : \n Property Name: "
                    + propertyName
                    + "\t Propert Value : "
                    + propertyValue);
            dataToStore.put(propertyName, propertyValue);

          } catch (IllegalAccessException
              | IllegalArgumentException
              | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
          }
        }
      }
      String studyCollectionName = AppUtil.makeStudyCollectionName(studyMetadataBean.getStudyId());

      logger.info("saveStudyMetadata() : \n Study Collection Name: " + studyCollectionName);
      responsesDao.saveStudyMetadata(
          studyCollectionName, studyMetadataBean.getStudyId(), dataToStore);
      logger.debug(
          "saveStudyMetadata() : \n Study Collection Name: "
              + studyCollectionName
              + " added successfully");
    } catch (IntrospectionException e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    }
  }

  @Override
  public QuestionnaireActivityStructureBean getStudyActivityMetadata(
      String orgId,
      String applicationId,
      StudyActivityMetadataRequestBean studyActivityMetadataRequestBean)
      throws ProcessResponseException {
    logger.debug("getStudyActivityMetadata() - starts ");
    HttpHeaders headers = null;

    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.ORG_ID_HEADER, orgId);
      headers.set(AppConstants.APPLICATION_ID_HEADER_WCP, applicationId);
      headers.set(AppConstants.AUTHORIZATION_HEADER, this.getWcpAuthorizationHeader());

      UriComponentsBuilder studyMetadataUriBuilder =
          UriComponentsBuilder.fromHttpUrl(appConfig.getWcpStudyActivityMetadataUrl())
              .queryParam(
                  AppConstants.STUDY_ID_PARAM, studyActivityMetadataRequestBean.getStudyId())
              .queryParam(
                  AppConstants.ACTIVITY_ID_KEY, studyActivityMetadataRequestBean.getActivityId())
              .queryParam(
                  AppConstants.ACTIVITY_VERSION_PARAM,
                  studyActivityMetadataRequestBean.getActivityVersion());
      logger.debug(studyMetadataUriBuilder.toUriString());
      responseEntity =
          restTemplate.exchange(
              studyMetadataUriBuilder.toUriString(),
              HttpMethod.GET,
              new HttpEntity<>(headers),
              QuestionnaireActivityMetaDataBean.class);
      QuestionnaireActivityMetaDataBean metadataParentBean =
          (QuestionnaireActivityMetaDataBean) responseEntity.getBody();
      QuestionnaireActivityStructureBean retQuestionnaireActivityStructureBean =
          metadataParentBean.getActivity();
      logger.debug("getStudyActivityMetadata() - ends");
      return retQuestionnaireActivityStructureBean;

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    }
  }

  private String getWcpAuthorizationHeader() throws ProcessResponseException {
    try {
      String wcpAuthUserName = appConfig.getWcpBundleId();
      String wcpAuthPassword = appConfig.getWcpAppToken();
      // Get Base64 String Representation of username and password
      if (!StringUtils.isBlank(wcpAuthUserName) && !StringUtils.isBlank(wcpAuthPassword)) {
        String wcpAuthStr = wcpAuthUserName + ":" + wcpAuthPassword;
        String wcpAuthStrEncoded = Base64.getEncoder().encodeToString(wcpAuthStr.getBytes());
        return AppConstants.BASIC_PREFIX + wcpAuthStrEncoded;
      } else {
        throw new ProcessResponseException(
            "Could not create AUthorization header for WCP as credentials are null.");
      }
    } catch (Exception e) {
      throw new ProcessResponseException("Could not create AUthorization header for WCP");
    }
  }
}
