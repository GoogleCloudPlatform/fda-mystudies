

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for mystudies_participant_datastore
DROP DATABASE IF EXISTS `mystudies_participant_datastore`;
CREATE DATABASE IF NOT EXISTS `mystudies_participant_datastore` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `mystudies_participant_datastore`;

-- Dumping structure for table mystudies_participant_datastore.app_info
DROP TABLE IF EXISTS `app_info`;
CREATE TABLE IF NOT EXISTS `app_info` (
  `id` varchar(255) NOT NULL,
  `android_bundle_id` varchar(64) DEFAULT NULL,
  `android_server_key` varchar(255) DEFAULT NULL,
  `app_description` longtext,
  `custom_app_id` varchar(15) NOT NULL,
  `app_name` varchar(64) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `forgot_email_body` longtext,
  `forgot_email_sub` varchar(255) DEFAULT NULL,
  `from_email_id` varchar(255) DEFAULT NULL,
  `from_email_password` varchar(255) DEFAULT NULL,
  `ios_bundle_id` varchar(64) DEFAULT NULL,
  `ios_certificate` longtext,
  `ios_certificate_password` varchar(64) DEFAULT NULL,
  `method_handler` int(11) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `reg_email_body` longtext,
  `reg_email_sub` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mghpamjyxooxd77dchdkmxjwk` (`custom_app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.app_permissions
DROP TABLE IF EXISTS `app_permissions`;
CREATE TABLE IF NOT EXISTS `app_permissions` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `edit` int(11) DEFAULT NULL,
  `app_info_id` varchar(255) DEFAULT NULL,
  `ur_admin_user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqu3t8ydahss7o038fwsir90aj` (`app_info_id`),
  KEY `FKo3o4so8vnqbekof4xnbve1q89` (`ur_admin_user_id`),
  CONSTRAINT `FKo3o4so8vnqbekof4xnbve1q89` FOREIGN KEY (`ur_admin_user_id`) REFERENCES `ur_admin_user` (`id`),
  CONSTRAINT `FKqu3t8ydahss7o038fwsir90aj` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.audit_events
DROP TABLE IF EXISTS `audit_events`;
CREATE TABLE IF NOT EXISTS `audit_events` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `event_request` json NOT NULL,
  `http_status_code` int(11) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `retry_count` bigint(20) DEFAULT NULL,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.auth_info
DROP TABLE IF EXISTS `auth_info`;
CREATE TABLE IF NOT EXISTS `auth_info` (
  `id` varchar(255) NOT NULL,
  `android_app_version` varchar(64) DEFAULT NULL,
  `auth_key` varchar(64) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `device_token` longtext,
  `device_type` varchar(64) DEFAULT NULL,
  `ios_app_version` varchar(64) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `remote_notification_flag` bit(1) DEFAULT NULL,
  `app_info_id` varchar(255) NOT NULL,
  `user_details_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8j1wwvyet40uoxl6qtiygixhk` (`app_info_id`),
  KEY `FKil5ax9t3bmtnmodgajy1c7edk` (`user_details_id`),
  KEY `auth_info_device_type_idx` (`device_type`),
  CONSTRAINT `FK8j1wwvyet40uoxl6qtiygixhk` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`id`),
  CONSTRAINT `FKil5ax9t3bmtnmodgajy1c7edk` FOREIGN KEY (`user_details_id`) REFERENCES `user_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.hibernate_sequence
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.locations
DROP TABLE IF EXISTS `locations`;
CREATE TABLE IF NOT EXISTS `locations` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `custom_id` varchar(64) NOT NULL,
  `description` longtext,
  `is_default` varchar(1) NOT NULL DEFAULT 'N',
  `updated_time` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_a1m10y1e7v3wu2497hh138k5i` (`custom_id`),
  UNIQUE KEY `name` (`name`),
  KEY `locations_status_idx` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.login_attempts
DROP TABLE IF EXISTS `login_attempts`;
CREATE TABLE IF NOT EXISTS `login_attempts` (
  `id` varchar(255) NOT NULL,
  `no_of_attempts` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.participant_registry_site
DROP TABLE IF EXISTS `participant_registry_site`;
CREATE TABLE IF NOT EXISTS `participant_registry_site` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `disabled_time` datetime DEFAULT NULL,
  `email` varchar(320) DEFAULT NULL,
  `enrollment_token` varchar(32) DEFAULT NULL,
  `enrollment_token_expiry` datetime DEFAULT NULL,
  `invitation_count` bigint(20) NOT NULL DEFAULT '0',
  `invitation_time` datetime DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `onboarding_status` varchar(3) DEFAULT NULL,
  `site_id` varchar(255) DEFAULT NULL,
  `study_info_id` varchar(255) DEFAULT NULL,
  `enrollment_token_used` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `participant_registry_site_email_study_info_token_uidx` (`email`,`study_info_id`,`enrollment_token`),
  UNIQUE KEY `UK_f8207wc2n5trl8demmfsvxis1` (`enrollment_token`),
  KEY `FKa0f0un45iyajvjfqq6ok42lqc` (`site_id`),
  KEY `FKrtseodvj7n9yjtwfqiixtviec` (`study_info_id`),
  KEY `participant_registry_site_site_id_onboarding_status_idx` (`site_id`,`onboarding_status`),
  CONSTRAINT `FKa0f0un45iyajvjfqq6ok42lqc` FOREIGN KEY (`site_id`) REFERENCES `sites` (`id`),
  CONSTRAINT `FKrtseodvj7n9yjtwfqiixtviec` FOREIGN KEY (`study_info_id`) REFERENCES `study_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.participant_study_info
DROP TABLE IF EXISTS `participant_study_info`;
CREATE TABLE IF NOT EXISTS `participant_study_info` (
  `id` varchar(255) NOT NULL,
  `adherence` int(11) DEFAULT NULL,
  `bookmark` bit(1) DEFAULT NULL,
  `completion` int(11) DEFAULT NULL,
  `consent_status` bit(1) DEFAULT NULL,
  `eligibility` bit(1) DEFAULT NULL,
  `enrolled_time` datetime DEFAULT NULL,
  `participant_id` varchar(64) DEFAULT NULL,
  `data_sharing_status` varchar(64) DEFAULT NULL,
  `status` varchar(64) DEFAULT NULL,
  `withdrawal_time` datetime DEFAULT NULL,
  `participant_registry_site_id` varchar(255) DEFAULT NULL,
  `site_id` varchar(255) DEFAULT NULL,
  `study_info_id` varchar(255) DEFAULT NULL,
  `user_details_id` varchar(255) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `participant_study_info_user_details_id_study_info_id__uidx` (`user_details_id`,`study_info_id`),
  UNIQUE KEY `UK_wic7o2oog14p35skw71ix3q0` (`participant_id`),
  KEY `FKb9362vga03lqkb0k46wsmi53x` (`participant_registry_site_id`),
  KEY `FKeppgsoyc8ldsx8mciwjo49j9u` (`site_id`),
  KEY `FKog8x3evjo4h227yc1jgtm2m4u` (`study_info_id`),
  KEY `participant_study_info_site_id_status_idx` (`site_id`,`status`),
  CONSTRAINT `FKb9362vga03lqkb0k46wsmi53x` FOREIGN KEY (`participant_registry_site_id`) REFERENCES `participant_registry_site` (`id`),
  CONSTRAINT `FKeppgsoyc8ldsx8mciwjo49j9u` FOREIGN KEY (`site_id`) REFERENCES `sites` (`id`),
  CONSTRAINT `FKodfgu8how5y9w4n048u2k4q79` FOREIGN KEY (`user_details_id`) REFERENCES `user_details` (`id`),
  CONSTRAINT `FKog8x3evjo4h227yc1jgtm2m4u` FOREIGN KEY (`study_info_id`) REFERENCES `study_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.personalized_user_report
DROP TABLE IF EXISTS `personalized_user_report`;
CREATE TABLE IF NOT EXISTS `personalized_user_report` (
  `id` varchar(255) NOT NULL,
  `activity_date_time` datetime DEFAULT NULL,
  `report_content` longtext,
  `report_title` varchar(128) DEFAULT NULL,
  `study_info_id` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlc6i0t4lygxvvkorf3dsridox` (`study_info_id`),
  KEY `FKo472wtrii4vfs2ouur00t696` (`user_id`),
  CONSTRAINT `FKlc6i0t4lygxvvkorf3dsridox` FOREIGN KEY (`study_info_id`) REFERENCES `study_info` (`id`),
  CONSTRAINT `FKo472wtrii4vfs2ouur00t696` FOREIGN KEY (`user_id`) REFERENCES `user_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.sites
DROP TABLE IF EXISTS `sites`;
CREATE TABLE IF NOT EXISTS `sites` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `target_enrollment` int(11) DEFAULT NULL,
  `location_id` varchar(255) DEFAULT NULL,
  `study_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbt0ribr7l9fnxpx549o2rrqy3` (`location_id`),
  KEY `FKii2p6mi7qcuwjl8613j0wam6` (`study_id`),
  KEY `sites_location_id_status_idx` (`location_id`,`status`),
  CONSTRAINT `FKbt0ribr7l9fnxpx549o2rrqy3` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`),
  CONSTRAINT `FKii2p6mi7qcuwjl8613j0wam6` FOREIGN KEY (`study_id`) REFERENCES `study_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.sites_permissions
DROP TABLE IF EXISTS `sites_permissions`;
CREATE TABLE IF NOT EXISTS `sites_permissions` (
  `id` varchar(255) NOT NULL,
  `edit` int(11) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `app_info_id` varchar(255) DEFAULT NULL,
  `site_id` varchar(255) DEFAULT NULL,
  `study_id` varchar(255) DEFAULT NULL,
  `ur_admin_user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtcdk83k4i02tjuags2cpu8acn` (`app_info_id`),
  KEY `FK4cuace13u7fwqshex5yrysb87` (`site_id`),
  KEY `FKpecdq337n7mvhy73p5by3d13e` (`study_id`),
  KEY `FK89o9erfie9bst1wa3dh01q90w` (`ur_admin_user_id`),
  CONSTRAINT `FK4cuace13u7fwqshex5yrysb87` FOREIGN KEY (`site_id`) REFERENCES `sites` (`id`),
  CONSTRAINT `FK89o9erfie9bst1wa3dh01q90w` FOREIGN KEY (`ur_admin_user_id`) REFERENCES `ur_admin_user` (`id`),
  CONSTRAINT `FKpecdq337n7mvhy73p5by3d13e` FOREIGN KEY (`study_id`) REFERENCES `study_info` (`id`),
  CONSTRAINT `FKtcdk83k4i02tjuags2cpu8acn` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.study_consent
DROP TABLE IF EXISTS `study_consent`;
CREATE TABLE IF NOT EXISTS `study_consent` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `pdf` longtext,
  `pdfPath` varchar(255) DEFAULT NULL,
  `pdfStorage` int(11) NOT NULL,
  `status` varchar(64) DEFAULT NULL,
  `version` varchar(64) DEFAULT NULL,
  `participant_study_id` varchar(255) DEFAULT NULL,
  `study_info_id` varchar(255) DEFAULT NULL,
  `user_details_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK585y9moi8wkwmurn0w54ebl36` (`participant_study_id`),
  KEY `FKck8ax0pv7ehm0tsyv4lrch3x0` (`study_info_id`),
  KEY `FK4t7tgs2ts7a40vqhjqhnro9h7` (`user_details_id`),
  CONSTRAINT `FK4t7tgs2ts7a40vqhjqhnro9h7` FOREIGN KEY (`user_details_id`) REFERENCES `user_details` (`id`),
  CONSTRAINT `FK585y9moi8wkwmurn0w54ebl36` FOREIGN KEY (`participant_study_id`) REFERENCES `participant_study_info` (`id`),
  CONSTRAINT `FKck8ax0pv7ehm0tsyv4lrch3x0` FOREIGN KEY (`study_info_id`) REFERENCES `study_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.study_info
DROP TABLE IF EXISTS `study_info`;
CREATE TABLE IF NOT EXISTS `study_info` (
  `id` varchar(255) NOT NULL,
  `category` varchar(64) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `custom_id` varchar(32) NOT NULL,
  `description` longtext,
  `enrolling` varchar(3) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `sponsor` varchar(32) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `tagline` varchar(64) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `app_info_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `study_info_custom_id_app_info_id_uidx` (`custom_id`,`app_info_id`),
  KEY `FK7q83jdpn6sguh4ly7fi8ahb7o` (`app_info_id`),
  KEY `study_info_name_idx` (`name`),
  CONSTRAINT `FK7q83jdpn6sguh4ly7fi8ahb7o` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.study_permissions
DROP TABLE IF EXISTS `study_permissions`;
CREATE TABLE IF NOT EXISTS `study_permissions` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `edit` int(11) DEFAULT NULL,
  `app_info_id` varchar(255) DEFAULT NULL,
  `study_id` varchar(255) DEFAULT NULL,
  `ur_admin_user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9lgwsrqrpnx1pk2cfqdg7legi` (`app_info_id`),
  KEY `FK6j7ma1f3bq4j7v607fohqwj57` (`study_id`),
  KEY `FKnh034h5kkise2bj47vwne1ibf` (`ur_admin_user_id`),
  CONSTRAINT `FK6j7ma1f3bq4j7v607fohqwj57` FOREIGN KEY (`study_id`) REFERENCES `study_info` (`id`),
  CONSTRAINT `FK9lgwsrqrpnx1pk2cfqdg7legi` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`id`),
  CONSTRAINT `FKnh034h5kkise2bj47vwne1ibf` FOREIGN KEY (`ur_admin_user_id`) REFERENCES `ur_admin_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.ur_admin_user
DROP TABLE IF EXISTS `ur_admin_user`;
CREATE TABLE IF NOT EXISTS `ur_admin_user` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `email` varchar(320) NOT NULL,
  `email_changed` bit(1) DEFAULT NULL,
  `first_name` varchar(128) DEFAULT NULL,
  `last_name` varchar(128) DEFAULT NULL,
  `location_permission` int(11) DEFAULT NULL,
  `phone_number` varchar(32) DEFAULT NULL,
  `security_code` varchar(64) DEFAULT NULL,
  `security_code_expire_date` timestamp NULL DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `super_admin` bit(1) DEFAULT NULL,
  `ur_admin_auth_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h5cwsps1f5ystnq7q231u8gjg` (`email`),
  UNIQUE KEY `UK_ndg2iywohvk6h4hvm1foxog2k` (`ur_admin_auth_id`),
  KEY `ur_admin_user_security_code_idx` (`security_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.user_app_details
DROP TABLE IF EXISTS `user_app_details`;
CREATE TABLE IF NOT EXISTS `user_app_details` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `app_info_id` varchar(255) DEFAULT NULL,
  `user_details_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt5yd3vwu2b10fh5ks9v6aq0x1` (`app_info_id`),
  KEY `FK3xeh7u54ii6khj0wfg5lifn67` (`user_details_id`),
  CONSTRAINT `FK3xeh7u54ii6khj0wfg5lifn67` FOREIGN KEY (`user_details_id`) REFERENCES `user_details` (`id`),
  CONSTRAINT `FKt5yd3vwu2b10fh5ks9v6aq0x1` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.user_details
DROP TABLE IF EXISTS `user_details`;
CREATE TABLE IF NOT EXISTS `user_details` (
  `id` varchar(255) NOT NULL,
  `code_expire_time` datetime DEFAULT NULL,
  `email` varchar(320) DEFAULT NULL,
  `email_code` varchar(32) DEFAULT NULL,
  `first_name` varchar(128) DEFAULT NULL,
  `last_name` varchar(128) DEFAULT NULL,
  `local_notification_flag` bit(1) DEFAULT NULL,
  `locale` varchar(128) DEFAULT NULL,
  `reminder_lead_time` varchar(64) DEFAULT NULL,
  `remote_notification_flag` bit(1) DEFAULT NULL,
  `security_token` varchar(128) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `touch_id` bit(1) DEFAULT NULL,
  `use_pass_code` bit(1) DEFAULT NULL,
  `user_id` varchar(64) DEFAULT NULL,
  `verification_time` datetime DEFAULT NULL,
  `app_info_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_details_user_id_app_info_id_uidx` (`user_id`,`app_info_id`),
  KEY `user_details_email_idx` (`email`),
  KEY `user_details_last_name_idx` (`last_name`),
  KEY `FKo905twpc0drywmf4x5e0io0cn` (`app_info_id`),
  CONSTRAINT `FKo905twpc0drywmf4x5e0io0cn` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.user_institution
DROP TABLE IF EXISTS `user_institution`;
CREATE TABLE IF NOT EXISTS `user_institution` (
  `id` varchar(255) NOT NULL,
  `institution_id` varchar(128) DEFAULT NULL,
  `user_details_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrwwx4csj1ske5o4m74lmusqbv` (`user_details_id`),
  CONSTRAINT `FKrwwx4csj1ske5o4m74lmusqbv` FOREIGN KEY (`user_details_id`) REFERENCES `user_details` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Dumping structure for table mystudies_participant_datastore.user_account_email_scheduler_tasks
DROP TABLE IF EXISTS `user_account_email_scheduler_tasks`;
CREATE TABLE IF NOT EXISTS `user_account_email_scheduler_tasks` (
  `id` varchar(255) NOT NULL,
  `app_id` varchar(255) DEFAULT NULL,
  `app_version` varchar(255) DEFAULT NULL,
  `correlation_id` varchar(255) DEFAULT NULL,
  `email_template_type` varchar(255) DEFAULT NULL,
  `mobile_platform` varchar(255) DEFAULT NULL,
  `sign_in_user_id` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `user_id` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Data exporting was unselected.

-- Dumping structure for table mystudies_participant_datastore.invite_participants
DROP TABLE IF EXISTS `invite_participants`;
CREATE TABLE IF NOT EXISTS `invite_participants` (
  `id` varchar(255) NOT NULL,
  `participant_registry_site_id` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `study_info_id` varchar(255) DEFAULT NULL,
  `app_id` varchar(255) DEFAULT NULL,
  `app_version` varchar(255) DEFAULT NULL,
  `correlation_id` varchar(255) DEFAULT NULL,
  `mobile_platform` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

