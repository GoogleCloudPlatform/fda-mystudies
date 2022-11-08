/* ISSUE #3911 Ability to manage apps via the Study Builder
ISSUE #3909 Provision to configure app-specific email addresses */
USE `fda_hphc`;

CREATE TABLE IF NOT EXISTS `apps` (
  `id` varchar(255) NOT NULL,
  `android_app_distributed` bit(1) DEFAULT NULL,
  `android_bundle_id` varchar(255) DEFAULT NULL,
  `android_server_key` varchar(255) DEFAULT NULL,
  `app_launched_date` varchar(255) DEFAULT NULL,
  `app_platform` varchar(255) DEFAULT NULL,
  `app_privacy_url` varchar(255) DEFAULT NULL,
  `apps_status` varchar(255) DEFAULT NULL,
  `app_store_url` varchar(255) DEFAULT NULL,
  `app_support_email_address` varchar(255) DEFAULT NULL,
  `app_terms_url` varchar(255) DEFAULT NULL,
  `app_website_url` varchar(255) DEFAULT NULL,
  `contact_us_address` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `custom_app_id` varchar(255) DEFAULT NULL,
  `feedback_email_address` varchar(255) DEFAULT NULL,
  `from_email_address` varchar(255) DEFAULT NULL,
  `has_app_draft` int DEFAULT NULL,
  `ios_latest_app_build_version` varchar(255) DEFAULT NULL,
  `ios_app_distributed` bit(1) DEFAULT NULL,
  `ios_bundle_id` varchar(255) DEFAULT NULL,
  `ios_server_key` varchar(255) DEFAULT NULL,
  `ios_latest_xcode_app_version` varchar(255) DEFAULT NULL,
  `is_app_published` bit(1) DEFAULT NULL,
  `is_live` int DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `organization_name` varchar(255) DEFAULT NULL,
  `play_store_url` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `version` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `app_permission` (
  `id` varchar(255) NOT NULL,
  `app_id` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `view_permission` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `app_sequence` (
  `app_sequence_id` varchar(255) NOT NULL,
  `actions` char(1) DEFAULT NULL,
  `app_check_list` char(1) DEFAULT NULL,
  `app_dashboard_chart` char(1) DEFAULT NULL,
  `app_dashboard_stats` char(1) DEFAULT NULL,
  `app_id` varchar(255) DEFAULT NULL,
  `app_info` char(1) DEFAULT NULL,
  `app_miscellaneous_branding` char(1) DEFAULT NULL,
  `app_properties` char(1) DEFAULT NULL,
  `app_settings` char(1) DEFAULT NULL,
  `developer_configs` char(1) DEFAULT NULL,
  PRIMARY KEY (`app_sequence_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO user_permissions (permission_id , permissions) VALUES (9, 'ROLE_CREATE_MANAGE_APPS'), (10, 'ROLE_MANAGE_APPS');


ALTER TABLE `fda_hphc`.`studies` ADD COLUMN `is_cloud_storage_moved` INT(10) NULL DEFAULT '0';

ALTER TABLE `fda_hphc`.`studies` ADD COLUMN `destination_custom_study_id` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_general_ci';
ALTER TABLE `fda_hphc`.`studies` ADD COLUMN `export_signed_url` VARCHAR(1012) NULL DEFAULT NULL COLLATE 'utf8_general_ci';
ALTER TABLE `fda_hphc`.`studies` ADD COLUMN `export_time` DATETIME(6) NULL DEFAULT NULL;

ALTER TABLE `fda_hphc`.`questionnaires` ADD COLUMN `sequence_number` INT(10) NULL DEFAULT NULL;
ALTER TABLE `fda_hphc`.`active_task_custom_frequencies` ADD COLUMN `sequence_number` INT(10) NULL DEFAULT NULL;
ALTER TABLE `fda_hphc`.`active_task_frequencies` ADD COLUMN `sequence_number` INT(10) NULL DEFAULT NULL;
ALTER TABLE `fda_hphc`.`notification` ADD COLUMN `sequence_number` INT(10) NULL DEFAULT NULL;
ALTER TABLE `fda_hphc`.`questionnaires_custom_frequencies` ADD COLUMN `sequence_number` INT(10) NULL DEFAULT NULL;
ALTER TABLE `fda_hphc`.`questionnaires_frequencies` ADD COLUMN `sequence_number` INT(10) NULL DEFAULT NULL;
ALTER TABLE `fda_hphc`.`response_sub_type_value` ADD COLUMN `sequence_number` INT(10) NULL DEFAULT NULL;


	
	
	

