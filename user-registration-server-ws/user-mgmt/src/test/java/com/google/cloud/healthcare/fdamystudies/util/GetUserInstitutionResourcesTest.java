package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import com.google.cloud.healthcare.fdamystudies.beans.UserInstitutionResources;
import com.google.cloud.healthcare.fdamystudies.model.UserInstitution;
import com.google.cloud.healthcare.fdamystudies.repository.UserInstitutionRepository;
import com.google.cloud.healthcare.fdamystudies.service.CloudStorageService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
public class GetUserInstitutionResourcesTest {
    @MockBean
    UserInstitutionRepository userInstitutionRepository;

    @MockBean
    CloudStorageService cloudStorageService;

    @Autowired
    GetUserInstitutionResources getUserInstitutionResources;

    @Test
    public void noUserFound() {
        assertFalse(getUserInstitutionResources.getInstitutionResourcesForUser(
                "missing_id").isPresent());
        verify(userInstitutionRepository, times(1)).findByUserUserId("missing_id");
    }

    @Test
    public void noInstitutionResourcesFound() {
        String fakeInstitution = "fake_institution";
        Mockito.when(userInstitutionRepository.findByUserUserId("fake_user_id")).thenReturn(Optional.of(UserInstitution.builder()
                .institutionId(fakeInstitution)
                .build()));
        Mockito.when(cloudStorageService.getAllInstitutionResources(
                "fake_institution")).thenReturn(new ArrayList<ByteArrayOutputStream>());

        assertFalse(getUserInstitutionResources.getInstitutionResourcesForUser(
                "fake_user_id").isPresent());
        verify(cloudStorageService, times(1)).getAllInstitutionResources(fakeInstitution);

    }

    @Test
    public void returnsInstitution() throws IOException {
        URL path = ClassLoader.getSystemResource("fake_html.html");
        File f = new File(path.getFile());
        byte[] bytes = FileUtils.readFileToByteArray(f);
        String fakeInstitution = "fake_institution";
        Mockito.when(userInstitutionRepository.findByUserUserId("fake_user_id"))
                .thenReturn(Optional.of(UserInstitution.builder()
                        .institutionId(fakeInstitution)
                        .build()));
        String html = "<p>fake html</p>";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(bytes);
        ArrayList<ByteArrayOutputStream> streams = new ArrayList<>();
        streams.add(byteArrayOutputStream);
        Mockito.when(cloudStorageService.getAllInstitutionResources(
                fakeInstitution)).thenReturn(streams);
        Optional<UserInstitutionResources> resources =
                getUserInstitutionResources.getInstitutionResourcesForUser(
                        "fake_user_id");
        assertTrue(resources.isPresent());
        assertTrue(resources.get().resources.size() == 1);
        assertThat(resources.get().resources.get(0).equals(html));

    }
}
