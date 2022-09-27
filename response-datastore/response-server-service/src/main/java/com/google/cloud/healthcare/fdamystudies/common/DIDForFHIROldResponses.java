package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.QUESTIONNAIRE_RESPONSE_TYPE;

import com.google.cloud.healthcare.fdamystudies.bean.FHIRQuestionnaireResponseBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.mapper.ConsentManagementAPIs;
import com.google.cloud.healthcare.fdamystudies.response.model.FHIRresponseEntity;
import com.google.cloud.healthcare.fdamystudies.service.ActivityResponseProcessorServiceImpl;
import com.google.cloud.healthcare.fdamystudies.utils.DeIdentifyHealthcareApis;
import com.google.cloud.healthcare.fdamystudies.utils.FhirHealthcareApis;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * For Update old DID or FHIR
 *
 * @author
 */
@Component
public class DIDForFHIROldResponses {

  private XLogger logger = XLoggerFactory.getXLogger(DIDForFHIROldResponses.class.getName());
  @Autowired private ApplicationConfiguration appConfig;
  @Autowired private ActivityResponseProcessorServiceImpl activityResponseProcessorServiceImpl;
  @Autowired private DeIdentifyHealthcareApis deIdentifyHealthcareApis;
  @Autowired private CommonDao commonDao;
  @Autowired private FhirHealthcareApis fhirHealthcareAPIs;
  @Autowired private ConsentManagementAPIs consentManagementAPIs;

  private static final String DATASET_PATH = "projects/%s/locations/%s/datasets/%s";
  private static final String FHIR_STORES = "/fhirStores/";

  /** @throws Exception */
  @PostConstruct
  public void didFHIRUpdate() throws Exception {
    logger.entry("didFHIRUpdate() begins");

    if (appConfig.getEnableFhirManagementApi().equalsIgnoreCase("fhir&did")) {

      List<FHIRresponseEntity> fhirList = commonDao.getFhirDetails(false);
      if (!fhirList.isEmpty()) {
        for (FHIRresponseEntity fhiRresponseEntity : fhirList) {

          // Create request and configure any parameters.
          String parentName =
              String.format(
                  "projects/%s/locations/%s", appConfig.getProjectId(), appConfig.getRegionId());

          consentManagementAPIs.createDatasetInHealthcareAPI(
              fhiRresponseEntity.getStudyId(), parentName);

          String datasetPath =
              String.format(
                  DATASET_PATH,
                  appConfig.getProjectId(),
                  appConfig.getRegionId(),
                  fhiRresponseEntity.getStudyId());

          activityResponseProcessorServiceImpl.createFhirStore(
              datasetPath, "DID_" + fhiRresponseEntity.getStudyId());
          List<String> resourceIds = new ArrayList<>();
          resourceIds.add(fhiRresponseEntity.getQuestionnaireReference());
          resourceIds.add(fhiRresponseEntity.getPatientReference());
          deIdentifyHealthcareApis.deIdentification(
              datasetPath + FHIR_STORES + "FHIR_" + fhiRresponseEntity.getStudyId(),
              datasetPath + FHIR_STORES + "DID_" + fhiRresponseEntity.getStudyId(),
              resourceIds);
          commonDao.updateDidStatus(fhiRresponseEntity.getQuestionnaireReference());

          // for updating location values in DID
          String fhirJson =
              fhirHealthcareAPIs.fhirResourceGet(
                  datasetPath
                      + FHIR_STORES
                      + "DID_"
                      + fhiRresponseEntity.getStudyId()
                      + "/fhir/"
                      + fhiRresponseEntity.getQuestionnaireReference());

          FHIRQuestionnaireResponseBean fhirQuestionnaireResponseBean =
              new Gson().fromJson(fhirJson, FHIRQuestionnaireResponseBean.class);
          deIdentifyHealthcareApis.updateDIDResponseLocation(
              datasetPath, fhiRresponseEntity.getStudyId(), fhirQuestionnaireResponseBean);

          if (appConfig.getDiscardFhirAfterDid().equalsIgnoreCase("true")) {
            String resourceName =
                datasetPath
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
