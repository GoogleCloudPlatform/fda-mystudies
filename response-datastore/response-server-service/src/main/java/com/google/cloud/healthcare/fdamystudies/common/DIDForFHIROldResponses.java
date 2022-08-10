/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.QUESTIONNAIRE_RESPONSE_TYPE;

import com.google.cloud.healthcare.fdamystudies.bean.FHIRQuestionnaireResponseBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.response.model.FHIRresponseEntity;
import com.google.cloud.healthcare.fdamystudies.service.ActivityResponseProcessorServiceImpl;
import com.google.cloud.healthcare.fdamystudies.utils.DeIdentifyHealthcareAPIs;
import com.google.cloud.healthcare.fdamystudies.utils.FhirHealthcareAPIs;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DIDForFHIROldResponses {

  private XLogger logger = XLoggerFactory.getXLogger(DIDForFHIROldResponses.class.getName());
  @Autowired private ApplicationConfiguration appConfig;
  @Autowired private ActivityResponseProcessorServiceImpl activityResponseProcessorServiceImpl;
  @Autowired private DeIdentifyHealthcareAPIs deIdentifyHealthcareAPIs;
  @Autowired private CommonDao commonDao;
  @Autowired private FhirHealthcareAPIs fhirHealthcareAPIs;

  private static final String DATASET_PATH = "projects/%s/locations/%s/datasets/%s";
  private static final String FHIR_STORES = "/fhirStores/";

  @PostConstruct
  public void didFHIRUpdate() throws Exception {
    logger.entry("didFHIRUpdate() begins");
    if (appConfig.getEnableFHIRManagementAPI().equalsIgnoreCase("FHIR_DID")) {

      List<FHIRresponseEntity> fhirList = commonDao.getFhirDetails(false);
      if (!fhirList.isEmpty()) {
        for (FHIRresponseEntity fhiRresponseEntity : fhirList) {

          String datasetPathforFHIR =
              String.format(
                  DATASET_PATH,
                  appConfig.getProjectId(),
                  appConfig.getRegionId(),
                  fhiRresponseEntity.getStudyId());
          String datasetPathforDID =
              String.format(
                  DATASET_PATH,
                  appConfig.getProjectId(),
                  appConfig.getRegionId(),
                  fhiRresponseEntity.getStudyId());

          activityResponseProcessorServiceImpl.createFhirStore(
              datasetPathforDID, "DID_" + fhiRresponseEntity.getStudyId());
          List<String> resourceIds = new ArrayList<>();
          resourceIds.add(fhiRresponseEntity.getQuestionnaireReference());
          resourceIds.add(fhiRresponseEntity.getPatientReference());
          deIdentifyHealthcareAPIs.deIdentification(
              datasetPathforFHIR + FHIR_STORES + "FHIR_" + fhiRresponseEntity.getStudyId(),
              datasetPathforDID + FHIR_STORES + "DID_" + fhiRresponseEntity.getStudyId(),
              resourceIds);
          commonDao.updateDidStatus(fhiRresponseEntity.getQuestionnaireReference());

          // for updating location values in DID
          String fhirJson =
              fhirHealthcareAPIs.fhirResourceGet(
                  datasetPathforDID
                      + FHIR_STORES
                      + "DID_"
                      + fhiRresponseEntity.getStudyId()
                      + "/fhir/"
                      + fhiRresponseEntity.getQuestionnaireReference());

          FHIRQuestionnaireResponseBean fhirQuestionnaireResponseBean =
              new Gson().fromJson(fhirJson, FHIRQuestionnaireResponseBean.class);
          deIdentifyHealthcareAPIs.updateDIDResponseLocation(
              datasetPathforDID,
              "DID_" + fhiRresponseEntity.getStudyId(),
              fhirQuestionnaireResponseBean);

          if (appConfig.getDiscardFHIRAfterDID().equalsIgnoreCase("true")) {
            String resourceName =
                datasetPathforFHIR
                    + FHIR_STORES
                    + "FHIR_"
                    + fhiRresponseEntity.getStudyId()
                    + "/fhir/"
                    + QUESTIONNAIRE_RESPONSE_TYPE
                    + "/"
                    + fhiRresponseEntity.getQuestionnaireReference();
            fhirHealthcareAPIs.fhirResourceDelete(resourceName);
            /*String resourceNameForPatient =
                datasetPathforFHIR
                    + FHIR_STORES
                    + fhiRresponseEntity.getStudyId()
                    + "/fhir/"
                    + fhiRresponseEntity.getPatientReference();
            fhirHealthcareAPIs.fhirResourceDelete(resourceNameForPatient);*/
          }
        }
      }
      logger.exit("didFHIRUpdate() ends");
    }
  }
}
