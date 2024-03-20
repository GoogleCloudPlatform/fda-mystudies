/* ISSUE #616 Use FCM instead of APNS for iOS push notifications */


ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `sponsor_name` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `apple_app_store_url` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `mystudies_participant_datastore`.`app_info` ADD COLUMN `google_play_store_url` VARCHAR(255) DEFAULT NULL;