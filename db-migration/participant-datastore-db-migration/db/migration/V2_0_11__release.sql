

/* Column added for integrating Google Identity Platform with MyStudies */

ALTER TABLE `mystudies_participant_datastore`.`ur_admin_user` ADD `idp_user` CHAR(1)  DEFAULT 'N';

UPDATE `mystudies_participant_datastore`.`ur_admin_user` set `idp_user`='N' where `idp_user` IS NULL;

ALTER TABLE `mystudies_participant_datastore`.`ur_admin_user` ADD `phone_number` VARCHAR(16);

/* ISSUE #616 Use FCM instead of APNS for iOS push notifications */

ALTER TABLE `mystudies_participant_datastore`.`user_details` ADD COLUMN `device_type` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`user_details` ADD COLUMN `device_os` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`user_details` ADD COLUMN `mobile_platform` VARCHAR(255) DEFAULT NULL;


ALTER TABLE `mystudies_participant_datastore`.`study_consent` ADD COLUMN `DataSharingConsentArtifactPath` VARCHAR(255);

ALTER TABLE mystudies_participant_datastore.participant_study_info ADD COLUMN user_study_version VARCHAR(255);

ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `ios_authorization_token` VARCHAR(50);
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `ios_key_id` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_general_ci';
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `ios_team_id` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_general_ci';