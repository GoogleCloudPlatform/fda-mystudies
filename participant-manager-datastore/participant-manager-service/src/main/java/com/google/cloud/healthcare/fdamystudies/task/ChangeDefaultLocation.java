package com.google.cloud.healthcare.fdamystudies.task;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CLOSE_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN;

import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.repository.LocationRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ChangeDefaultLocation {
  private static final String DEFAULT_LOCATION_NAME = "defaultLocation_OpenStudy";

  private static final String DEFAULT_CUSTOM_ID = "default_OpnStdy";

  private XLogger logger =
      XLoggerFactory.getXLogger(EmailSentToInviteParticipantsScheduledTask.class.getName());

  @Autowired private LocationRepository locationRepository;
  @Autowired private SiteRepository siteRepository;

  @PostConstruct
  public void locationUpdates() {
    logger.entry("locationUpdates() begins");
    Optional<LocationEntity> optLocation = locationRepository.findByDefault();
    if (optLocation.isPresent()
        && optLocation.get().isDefault()
        && (!optLocation.get().getCustomId().equals(DEFAULT_CUSTOM_ID))) {
      List<String> closedStudyIds = locationRepository.findByDefaultLocationForStudy(CLOSE_STUDY);
      List<String> openStudyIds = locationRepository.findByDefaultLocationForStudy(OPEN);

      if (CollectionUtils.isNotEmpty(closedStudyIds) && CollectionUtils.isNotEmpty(openStudyIds)) {
        locationRepository.updateLocation();
        createLocation();
        Optional<LocationEntity> optionalLocation = locationRepository.findByDefault();
        siteRepository.updateForSite(optionalLocation.get().getId(), openStudyIds);
      } else if (CollectionUtils.isNotEmpty(closedStudyIds)) {
        locationRepository.updateLocation();
        createLocation();
      } else {
        LocationEntity location = optLocation.get();
        location.setCustomId(DEFAULT_CUSTOM_ID);
        location.setName(DEFAULT_LOCATION_NAME);
        location = locationRepository.saveAndFlush(location);
      }
    }

    logger.exit("locationUpdates() ends");
  }

  private void createLocation() {
    LocationEntity location = new LocationEntity();
    location.setCustomId(DEFAULT_CUSTOM_ID);
    location.setName(DEFAULT_LOCATION_NAME);
    location.setIsDefault("Y");
    location.setStatus(1);
    location = locationRepository.saveAndFlush(location);
  }
}
