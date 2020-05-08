/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.WriteChannel;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;

@Service
public class CloudStorageService {

    private Storage storageService;

    @Autowired
    private ApplicationPropertyConfiguration appConfig;

    @PostConstruct
    private void init() {
        storageService = StorageOptions.getDefaultInstance().getService();
    }

    public List<ByteArrayOutputStream> getAllInstitutionResources(String institutionId) {
        Bucket bucket = storageService.get(appConfig.getInstitutionBucketName());
        Page<Blob> blobs = bucket.list();

        ArrayList<ByteArrayOutputStream> streams = new ArrayList<>();
        for (Blob blob : blobs.iterateAll()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blob.downloadTo(outputStream);
            streams.add(outputStream);
        }
        return streams;
    }

}


