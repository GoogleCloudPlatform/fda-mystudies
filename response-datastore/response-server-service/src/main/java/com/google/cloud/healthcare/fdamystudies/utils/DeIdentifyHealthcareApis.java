/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.QUESTIONNAIRE_RESPONSE_TYPE;

import com.google.api.services.healthcare.v1.CloudHealthcare;
import com.google.api.services.healthcare.v1.CloudHealthcare.Projects.Locations.Datasets.FhirStores;
import com.google.api.services.healthcare.v1.model.DeidentifyConfig;
import com.google.api.services.healthcare.v1.model.DeidentifyFhirStoreRequest;
import com.google.api.services.healthcare.v1.model.FhirConfig;
import com.google.api.services.healthcare.v1.model.FhirFilter;
import com.google.api.services.healthcare.v1.model.FieldMetadata;
import com.google.api.services.healthcare.v1.model.Operation;
import com.google.api.services.healthcare.v1.model.Resources;
import com.google.cloud.healthcare.fdamystudies.bean.FHIRQuestionnaireResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ItemsQuestionnaireResponse;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeIdentifyHealthcareApis {

  private XLogger logger = XLoggerFactory.getXLogger(DeIdentifyHealthcareApis.class.getName());
  @Autowired private FhirHealthcareApis fhirHealthcareAPIs;

  private static final String FHIR_STORES = "/fhirStores/";

  private static final String FIELDS_TO_BE_DEIDENTIFIED =
      "identifier.type.text,item.definition,item.answer.valueString,item.answer.valueDecimal,item.answer.valueTime,item.item.definition,item.item.answer.valueString,item.item.answer.valueDecimal,item.item.answer.valueTime";

  private static final String INSPECT_AND_TRANSFORM = "INSPECT_AND_TRANSFORM";

  public void deIdentification(
      String srcDataStoreName, String destDataStoreName, List<String> resourceId)
      throws ProcessResponseException {
    logger.entry("begin deIdentification()");
    String fhirResponseFields = FIELDS_TO_BE_DEIDENTIFIED;
    try {
      // Initialize the client, which will be used to interact with the service.
      CloudHealthcare client = AppUtil.createClient();

      // Configure what information needs to be De-Identified.
      FieldMetadata fieldMetadata =
          new FieldMetadata()
              .setAction(INSPECT_AND_TRANSFORM)
              .setPaths(Arrays.asList(fhirResponseFields.split(",")));
      FhirConfig fhirConfig = new FhirConfig().setFieldMetadataList(Arrays.asList(fieldMetadata));
      DeidentifyConfig deidentifyConfig = new DeidentifyConfig().setFhir(fhirConfig);
      Resources resources = new Resources().setResources(resourceId);
      FhirFilter fhirFilter = new FhirFilter().setResources(resources);

      // Create the de-identify request and configure any parameters.
      DeidentifyFhirStoreRequest deidentifyRequest =
          new DeidentifyFhirStoreRequest()
              .setDestinationStore(destDataStoreName)
              .setConfig(deidentifyConfig)
              .setResourceFilter(fhirFilter);
      FhirStores.Deidentify request =
          client
              .projects()
              .locations()
              .datasets()
              .fhirStores()
              .deidentify(srcDataStoreName, deidentifyRequest);

      Operation operation = request.execute();
      while (operation.getDone() == null || !operation.getDone()) {
        // Update the status of the operation with another request.
        Thread.sleep(500); // Pause for 500ms between requests.
        operation =
            client
                .projects()
                .locations()
                .datasets()
                .operations()
                .get(operation.getName())
                .execute();
      }

      logger.debug("De-identified Dataset created. Response content: " + operation.getResponse());
      logger.exit("deIdentification() - Ends ");
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    }
  }

  public void updateDIDResponseLocation(
      String datasetPathforDID,
      String studyId,
      FHIRQuestionnaireResponseBean fhirQuestionnaireResponseBean)
      throws ProcessResponseException {
    if (fhirQuestionnaireResponseBean != null) {
      List<ItemsQuestionnaireResponse> items = fhirQuestionnaireResponseBean.getItem();
      int count = 0;
      for (ItemsQuestionnaireResponse questionnaireItems : items) {
        final String RESOURCE_NAME =
            datasetPathforDID
                + FHIR_STORES
                + "DID_"
                + studyId
                + "/fhir/"
                + QUESTIONNAIRE_RESPONSE_TYPE
                + "/"
                + fhirQuestionnaireResponseBean.getId();
        if (questionnaireItems.getDefinition().equalsIgnoreCase("location")) {
          String data =
              "[{\"op\": \"replace\", \"path\": \"/item/"
                  + count
                  + "/answer/0/valueString\", \"value\": \"[LOCATION]\"}]";
          fhirHealthcareAPIs.fhirResourcePatch(RESOURCE_NAME, data);
        } else if (questionnaireItems.getDefinition().equalsIgnoreCase("grouped")) {
          List<ItemsQuestionnaireResponse> innerItem = questionnaireItems.getItem();
          if (CollectionUtils.isNotEmpty(innerItem)) {
            int innerCount = 0;
            for (ItemsQuestionnaireResponse questionnaireInnerItems : innerItem) {
              if (questionnaireInnerItems.getDefinition().equalsIgnoreCase("location")) {
                String data =
                    "[{\"op\": \"replace\", \"path\": \"/item/"
                        + count
                        + "/item/"
                        + innerCount
                        + "/answer/0/valueString\", \"value\": \"[LOCATION]\"}]";
                fhirHealthcareAPIs.fhirResourcePatch(RESOURCE_NAME, data);
              }
              innerCount++;
            }
          }
        }
        count++;
      }
    }
  }
}
