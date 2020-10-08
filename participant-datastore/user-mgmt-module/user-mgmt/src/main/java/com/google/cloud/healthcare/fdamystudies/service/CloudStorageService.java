/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloudStorageService {
  private static Logger logger = LoggerFactory.getLogger(CloudStorageService.class);

  private Storage storageService;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @PostConstruct
  private void init() {
    storageService = StorageOptions.getDefaultInstance().getService();
  }

  @AllArgsConstructor
  @NoArgsConstructor
  public static class InstitutionResource {
    public String title;
    public ByteArrayOutputStream stream = new ByteArrayOutputStream();
    public String hash;
  }

  public List<InstitutionResource> getAllInstitutionResources(String institutionId) {
    Bucket bucket = null;

    bucket = storageService.get(appConfig.getInstitutionBucketName());

    Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(institutionId));

    ArrayList<InstitutionResource> resources = new ArrayList<>();
    for (Blob blob : blobs.iterateAll()) {
      InstitutionResource resource = new InstitutionResource();
      // Remove institutionId directory path from title.
      resource.title = blob.getName().replaceFirst(Pattern.quote(institutionId + "/"), "");
      resource.hash = blob.getMd5();
      // There are placeholder files in GCS that match the directory
      // but do not have a file name. Skip these.
      if (resource.title.isEmpty()) continue;
      blob.downloadTo(resource.stream);
      resources.add(resource);
    }
    return resources;
  }
}
