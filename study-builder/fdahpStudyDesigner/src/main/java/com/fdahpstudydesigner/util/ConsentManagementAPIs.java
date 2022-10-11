package com.fdahpstudydesigner.util;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.healthcare.v1.CloudHealthcare;
import com.google.api.services.healthcare.v1.CloudHealthcare.Projects.Locations.Datasets;
import com.google.api.services.healthcare.v1.CloudHealthcare.Projects.Locations.Datasets.ConsentStores;
import com.google.api.services.healthcare.v1.CloudHealthcare.Projects.Locations.Datasets.ConsentStores.ConsentArtifacts;
import com.google.api.services.healthcare.v1.CloudHealthcareScopes;
import com.google.api.services.healthcare.v1.model.ConsentArtifact;
import com.google.api.services.healthcare.v1.model.ConsentStore;
import com.google.api.services.healthcare.v1.model.Dataset;
import com.google.api.services.healthcare.v1.model.Image;
import com.google.api.services.healthcare.v1.model.Operation;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsentManagementAPIs {
  public static final JsonFactory JSON_FACTORY = new JacksonFactory();
  public static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  private XLogger logger = XLoggerFactory.getXLogger(ConsentManagementAPIs.class.getName());

  protected static final Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();

  private static CloudHealthcare createClient() throws IOException {
    // Use Application Default Credentials (ADC) to authenticate the requests
    // For more information see https://cloud.google.com/docs/authentication/production
    final GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(CloudHealthcareScopes.CLOUD_PLATFORM));

    // Create a HttpRequestInitializer, which will provide a baseline configuration to all requests.
    HttpRequestInitializer requestInitializer =
        new HttpRequestInitializer() {
          @Override
          public void initialize(com.google.api.client.http.HttpRequest httpRequest)
              throws IOException {
            new HttpCredentialsAdapter(credential).initialize(httpRequest);
            httpRequest.setConnectTimeout(60000); // 1 minutes connect timeout
            httpRequest.setReadTimeout(60000); // 1 minutes read timeout
          }
        };

    // Build the client for interacting with the service.
    return new CloudHealthcare.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
        .setApplicationName("your-application-name")
        .build();
  }

  public String createConsentArtifact(
      Map<String, String> metaData,
      String userId,
      String version,
      String gcsUri,
      String consentDatasetId,
      String consentStoreId)
      throws Exception {
    logger.entry("Begin createConsentArtifact()");
    try {
      CloudHealthcare client = createClient();

      String parentName =
          String.format(
              "projects/%s/locations/%s/datasets/%s/consentStores/%s",
              configMap.get("dataProjectId"),
              configMap.get("regionId"),
              consentDatasetId,
              consentStoreId);

      Image image = new Image();
      image.setGcsUri(gcsUri);
      List<Image> images = new ArrayList<>(Arrays.asList(image));

      ConsentArtifact content =
          new ConsentArtifact()
              .setMetadata(metaData)
              .setUserId(userId)
              .setConsentContentVersion(version)
              .setConsentContentScreenshots(images);

      ConsentArtifacts.Create request =
          client
              .projects()
              .locations()
              .datasets()
              .consentStores()
              .consentArtifacts()
              .create(parentName, content);

      ConsentArtifact response = request.execute();
      logger.info("ConsentArtifact created: " + response.toPrettyString());
      return response.toPrettyString();
    } catch (IOException e) {
      logger.error("Consent artifact creation failed with an exception", e);
      throw new Exception(FdahpStudyDesignerConstants.FAILURE_CONSENT_STORE_MESSAGE);
    }
  }

  public void createConsentStore(String consentStoreId, String consentDatasetId) throws Exception {
    logger.entry("begin fhirStoreCreate()");

    try {
      CloudHealthcare client = createClient();

      String parentName =
          String.format(
              "projects/%s/locations/%s/datasets/%s",
              configMap.get("dataProjectId"), configMap.get("regionId"), consentDatasetId);

      // Configure the FhirStore to be created.
      //      Map<String, String> labels = new HashMap<>();
      //      labels.put("env", "dev");
      //      String version = "R4";
      ConsentStore content = new ConsentStore();

      // Create request and configure any parameters.
      ConsentStores.Create request =
          client
              .projects()
              .locations()
              .datasets()
              .consentStores()
              .create(parentName, content)
              .setConsentStoreId(consentStoreId);

      // Execute the request and process the results.
      ConsentStore response = request.execute();

      logger.info("CONSENT store created: " + response.toPrettyString());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new Exception(e.getMessage());
    }
    logger.exit("consentStoreCreate() - Ends ");
  }

  public String consentStoreGet(String consentStoreName, String consentDatasetId) throws Exception {
    logger.entry("begin consnetStoreGet()");
    // Initialize the client, which will be used to interact with the service.
    try {
      CloudHealthcare client = createClient();

      String parentName =
          String.format(
              "projects/%s/locations/%s/datasets/%s/consentStores/%s",
              configMap.get("dataProjectId"),
              configMap.get("regionId"),
              consentDatasetId,
              consentStoreName);
      // Create request and configure any parameters.
      ConsentStores.Get request =
          client.projects().locations().datasets().consentStores().get(parentName);

      // Execute the request and process the results.
      ConsentStore store = request.execute();

      logger.exit("fhirStoreGet() - Ends ");
      return store.toPrettyString();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new Exception(e.getMessage());
    }
  }

  public void createDatasetInHealthcareAPI(String datasetId) throws IOException {

    logger.entry("Begin datasetCreateHealthcareAPI()");
    String projectId = configMap.get("dataProjectId");
    String regionId = configMap.get("regionId");

    try {
      // Initialize the client, which will be used to interact with the service.
      CloudHealthcare client = createClient();

      // Configure the dataset to be created.
      Dataset dataset = new Dataset();

      // Create request and configure any parameters.
      String parentName = String.format("projects/%s/locations/%s", projectId, regionId);
      Datasets.Create request =
          client.projects().locations().datasets().create(parentName, dataset);
      request.setDatasetId(datasetId);

      // Execute the request, wait for the operation to complete, and process the results.

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

    } catch (Exception ex) {
      logger.entry("Error datasetCreateHealthcareAPI(): ", ex);
    }
    logger.exit("End datasetCreateHealthcareAPI()");
  }
}
