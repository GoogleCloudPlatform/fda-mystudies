/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;

@Service
public class CloudStorageService {
    private static Logger logger = LoggerFactory.getLogger(CloudStorageService.class);

    private Storage storageService;

    @Autowired
    private ApplicationPropertyConfiguration appConfig;

    @PostConstruct
    private void init() {
        storageService = StorageOptions.getDefaultInstance().getService();
    }

    public List<ByteArrayOutputStream> getAllInstitutionResources(String institutionId) {
        Bucket bucket = null;
        try {
            bucket = storageService.get(appConfig.getInstitutionBucketName());
        } catch (StorageException e) {
            logger.error(e.getMessage());
        } finally {
            if (bucket == null) return new ArrayList<>();
        }
        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(institutionId));

        ArrayList<ByteArrayOutputStream> streams = new ArrayList<>();
        for (Blob blob : blobs.iterateAll()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blob.downloadTo(outputStream);
            streams.add(outputStream);
        }
        return streams;
    }

}


