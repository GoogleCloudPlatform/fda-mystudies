/* ISSUE #616 Use FCM instead of APNS for iOS push notifications */
ALTER TABLE mystudies_participant_datastore.app_info ADD ios_server_key VARCHAR(255) DEFAULT NULL;

ALTER TABLE `mystudies_participant_datastore`.`user_details` 
CHANGE COLUMN `verification_time` `verification_time` VARCHAR(255) NULL DEFAULT NULL ;

/* ISSUE #3911 Ability to manage apps via the Study Builder
ISSUE #3909 Provision to configure app-specific email addresses */
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `contact_us_to_email` VARCHAR(320) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `feedback_to_email` VARCHAR(320) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `app_support_email_address` VARCHAR(320) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `app_platform` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `app_store_url` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `app_privacy_url` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `play_store_url` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `app_terms_url` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `organization_name` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `ios_latest_xcode_app_version` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `ios_latest_app_build_version` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `ios_force_upgrade` int(11) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `android_latest_app_version` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `android_force_upgrade` int(11) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `type` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `app_website`  VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `app_status`  VARCHAR(255) DEFAULT 'Active';

ALTER TABLE `mystudies_participant_datastore`.`app_info` modify `android_force_upgrade` BIT DEFAULT NULL;

ALTER TABLE `mystudies_participant_datastore`.`app_info` modify `ios_force_upgrade` BIT DEFAULT NULL;
