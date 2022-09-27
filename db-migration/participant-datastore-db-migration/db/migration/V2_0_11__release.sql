/* ISSUE #616 Use FCM instead of APNS for iOS push notifications */

ALTER TABLE `mystudies_participant_datastore`.`user_details` ADD COLUMN `device_type` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`user_details` ADD COLUMN `device_os` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`user_details` ADD COLUMN `mobile_platform` VARCHAR(255) DEFAULT NULL;


ALTER TABLE `mystudies_participant_datastore`.`study_consent` ADD COLUMN `DataSharingConsentArtifactPath` VARCHAR(255);

ALTER TABLE mystudies_participant_datastore.participant_study_info ADD COLUMN user_study_version VARCHAR(255);