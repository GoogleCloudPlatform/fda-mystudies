/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityMetaDataBean;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyActivityMetadataRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.ResponsesDao;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
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
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
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

@Service
public class StudyMetadataServiceImpl implements StudyMetadataService {
  @Autowired private RestTemplate restTemplate;
  @Autowired private ApplicationConfiguration appConfig;

  @Autowired
  @Qualifier("cloudFirestoreResponsesDaoImpl")
  private ResponsesDao responsesDao;

  private XLogger logger = XLoggerFactory.getXLogger(StudyMetadataServiceImpl.class.getName());

  @Override
  public void saveStudyMetadata(StudyMetadataBean studyMetadataBean)
      throws ProcessResponseException, IntrospectionException, IllegalAccessException,
          IllegalArgumentException, InvocationTargetException {

    BeanInfo beanInfo;
    beanInfo = Introspector.getBeanInfo(studyMetadataBean.getClass());
    PropertyDescriptor[] propDescriptor = beanInfo.getPropertyDescriptors();
    Map<String, Object> dataToStore = new HashMap<>();
    for (PropertyDescriptor pd : propDescriptor) {
      String propertyName = pd.getName();
      if (!propertyName.equals("class")) {
        Method getterMethod = pd.getReadMethod();
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
      }
    }
    String studyCollectionName = AppUtil.makeStudyCollectionName(studyMetadataBean.getStudyId());

    logger.info("saveStudyMetadata() : \n Study Collection Name: " + studyCollectionName);
    responsesDao.saveStudyMetadata(
        studyCollectionName, studyMetadataBean.getStudyId(), dataToStore);
    logger.exit(
        "saveStudyMetadata() : \n Study Collection Name: "
            + studyCollectionName
            + " added successfully");
  }

  @Override
  public QuestionnaireActivityStructureBean getStudyActivityMetadata(
      String applicationId,
      StudyActivityMetadataRequestBean studyActivityMetadataRequestBean,
      AuditLogEventRequest auditRequest)
      throws ProcessResponseException {
    logger.entry("begin getStudyActivityMetadata()");
    HttpHeaders headers = null;

    ResponseEntity<?> responseEntity = null;
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AppConstants.APPLICATION_ID_HEADER_WCP, applicationId);
    headers.set(AppConstants.AUTHORIZATION_HEADER, this.getWcpAuthorizationHeader());
    AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

    UriComponentsBuilder studyMetadataUriBuilder =
        UriComponentsBuilder.fromHttpUrl(appConfig.getWcpStudyActivityMetadataUrl())
            .queryParam(AppConstants.STUDY_ID_PARAM, studyActivityMetadataRequestBean.getStudyId())
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
    logger.exit("getStudyActivityMetadata() - ends");
    return retQuestionnaireActivityStructureBean;
  }

  private String getWcpAuthorizationHeader() throws ProcessResponseException {
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
  }
}
