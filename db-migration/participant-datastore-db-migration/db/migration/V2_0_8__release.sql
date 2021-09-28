/* ISSUE #616 Use FCM instead of APNS for iOS push notifications */
ALTER TABLE mystudies_participant_datastore.app_info ADD ios_server_key VARCHAR(255) DEFAULT NULL;

ALTER TABLE `mystudies_participant_datastore`.`user_details` 
CHANGE COLUMN `verification_time` `verification_time` VARCHAR(255) NULL DEFAULT NULL ;
