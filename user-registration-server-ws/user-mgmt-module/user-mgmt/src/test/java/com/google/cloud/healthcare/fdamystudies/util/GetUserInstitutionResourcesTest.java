/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import com.google.cloud.healthcare.fdamystudies.beans.UserResourceBean;
import com.google.cloud.healthcare.fdamystudies.model.UserInstitutionEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserInstitutionRepository;
import com.google.cloud.healthcare.fdamystudies.service.CloudStorageService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

// TODO (#761) Added @Ignore to test classes written by UNC team, should be fixed later or next
// track

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
@Ignore
public class GetUserInstitutionResourcesTest {
  @MockBean UserInstitutionRepository userInstitutionRepository;

  @MockBean CloudStorageService cloudStorageService;

  @Autowired GetUserInstitutionResources getUserInstitutionResources;

  @Test
  public void noUserFound() {
    assertThat(
        getUserInstitutionResources.getInstitutionResourcesForUser("missing_id"), hasSize(0));
    verify(userInstitutionRepository, times(1)).findByUserUserId("missing_id");
  }

  @Test
  public void noInstitutionResourcesFound() {
    String fakeInstitution = "fake_institution";
    Mockito.when(userInstitutionRepository.findByUserUserId("fake_user_id"))
        .thenReturn(
            Optional.of(UserInstitutionEntity.builder().institutionId(fakeInstitution).build()));
    Mockito.when(cloudStorageService.getAllInstitutionResources("fake_institution"))
        .thenReturn(new ArrayList<>());

    assertThat(
        getUserInstitutionResources.getInstitutionResourcesForUser("fake_user_id"), hasSize(0));
    verify(cloudStorageService, times(1)).getAllInstitutionResources(fakeInstitution);
  }

  @Test
  public void returnsInstitution() throws IOException {
    String fakeInstitution = "fake_institution";
    Mockito.when(userInstitutionRepository.findByUserUserId("fake_user_id"))
        .thenReturn(
            Optional.of(UserInstitutionEntity.builder().institutionId(fakeInstitution).build()));
    String html = "<p>fake html</p>\n";
    URL path = ClassLoader.getSystemResource("fake_html.html");
    File f = new File(path.getFile());
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(FileUtils.readFileToByteArray(f));
    ArrayList<CloudStorageService.InstitutionResource> resources = new ArrayList<>();
    resources.add(
        new CloudStorageService.InstitutionResource(
            "fake_title.html", byteArrayOutputStream, "id"));
    Mockito.when(cloudStorageService.getAllInstitutionResources(fakeInstitution))
        .thenReturn(resources);
    List<UserResourceBean> userResourceBeans =
        getUserInstitutionResources.getInstitutionResourcesForUser("fake_user_id");
    assertThat(userResourceBeans, hasSize(1));
    assertThat(userResourceBeans.get(0).getTitle(), equalTo("fake_title.html"));
    assertThat(userResourceBeans.get(0).getContent(), equalTo(html));
    assertThat(userResourceBeans.get(0).getResourcesId(), equalTo("resources:id"));
  }
}
