package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.beans.UserInstitutionResources;
import com.google.cloud.healthcare.fdamystudies.model.UserInstitution;
import com.google.cloud.healthcare.fdamystudies.repository.UserInstitutionRepository;
import com.google.cloud.healthcare.fdamystudies.service.CloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GetUserInstitutionResources {
    @Autowired
    UserInstitutionRepository userInstitutionRepository;

    @Autowired
    CloudStorageService cloudStorageService;

    public Optional<UserInstitutionResources> getInstitutionResourcesForUser(String userId) {
        Optional<UserInstitution> maybeUserInstitution =
                userInstitutionRepository.findByUserUserId(userId);
        if (!maybeUserInstitution.isPresent()) return Optional.empty();
        UserInstitution userInstitution = maybeUserInstitution.get();

        List<ByteArrayOutputStream> streams =
                cloudStorageService.getAllInstitutionResources(userInstitution.getInstitutionId());
        if (streams.isEmpty()) return Optional.empty();

        UserInstitutionResources resources = new UserInstitutionResources();
        resources.resources = new ArrayList<>();
        for (ByteArrayOutputStream stream : streams) {
            resources.resources.add(new String(stream.toByteArray()));
        }
        return Optional.of(resources);
    }
}
