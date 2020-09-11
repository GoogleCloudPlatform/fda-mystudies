-- --------------------------------------------------------
-- Host:                         35.196.150.7
-- Server version:               5.7.25-google - (Google)
-- Server OS:                    Linux
-- HeidiSQL Version:             10.3.0.5771
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for mystudies_userregistration
CREATE DATABASE IF NOT EXISTS `mystudies_userregistration` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `mystudies_userregistration`;



-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.app_info
CREATE TABLE IF NOT EXISTS `app_info` (
  `app_info_id` int(11) NOT NULL AUTO_INCREMENT,
  `android_bundle_id` varchar(255) DEFAULT NULL,
  `android_server_key` varchar(255) DEFAULT NULL,
  `app_description` varchar(255) DEFAULT NULL,
  `custom_app_id` varchar(255) DEFAULT NULL,
  `app_name` varchar(255) DEFAULT NULL,
  `created_by` int(20) DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `forgot_email_body` varchar(255) DEFAULT NULL,
  `forgot_email_sub` varchar(255) DEFAULT NULL,
  `from_email_id` varchar(255) DEFAULT NULL,
  `from_email_password` varchar(255) DEFAULT NULL,
  `ios_bundle_id` varchar(255) DEFAULT NULL,
  `ios_certificate` text,
  `ios_certificate_password` varchar(255) DEFAULT NULL,
  `method_handler` tinyint(1) DEFAULT NULL,
  `modified_by` int(20) DEFAULT NULL,
  `modified_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reg_email_body` varchar(255) DEFAULT NULL,
  `reg_email_sub` varchar(255) DEFAULT NULL,
 
  PRIMARY KEY (`app_info_id`)
 
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.app_permissions
CREATE TABLE IF NOT EXISTS `app_permissions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int(20) DEFAULT '0',
  `edit` tinyint(1) DEFAULT '0',
  `app_info_id` int(11) DEFAULT NULL,
  `ur_admin_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqu3t8ydahss7o038fwsir90aj` (`app_info_id`),
  KEY `FKo3o4so8vnqbekof4xnbve1q89` (`ur_admin_user_id`),
  CONSTRAINT `FKo3o4so8vnqbekof4xnbve1q89` FOREIGN KEY (`ur_admin_user_id`) REFERENCES `ur_admin_user` (`id`),
  CONSTRAINT `FKqu3t8ydahss7o038fwsir90aj` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`app_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.auth_info
CREATE TABLE IF NOT EXISTS `auth_info` (
  `auth_id` int(11) NOT NULL,
  `_ts` datetime DEFAULT NULL,
  `android_app_version` varchar(255) DEFAULT NULL,
  `app_info_id` int(11) DEFAULT NULL,
  `auth_key` varchar(255) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `device_token` varchar(255) DEFAULT NULL,
  `device_type` varchar(255) DEFAULT NULL,
  `ios_app_version` varchar(255) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `remote_notification_flag` tinyint(1) DEFAULT NULL,
  `user_details_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.client_info
CREATE TABLE IF NOT EXISTS `client_info` (
  `client_info_id` int(11) NOT NULL,
  `application_id` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`client_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.hibernate_sequence
CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert 0 into hibernate_sequence if the table is empty.
INSERT INTO mystudies_userregistration.hibernate_sequence (next_val)
SELECT 0
WHERE NOT EXISTS (SELECT * FROM mystudies_userregistration.hibernate_sequence);

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.locations
CREATE TABLE IF NOT EXISTS `locations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NULL DEFAULT NULL,
  `created_by` int(20) DEFAULT '0',
  `custom_id` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_default` char(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.login_attempts
CREATE TABLE IF NOT EXISTS `login_attempts` (
  `id` int(11) NOT NULL,
  `no_of_attempts` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.mail_messages
CREATE TABLE IF NOT EXISTS `mail_messages` (
  `mail_messageid` int(11) NOT NULL AUTO_INCREMENT,
  `bcc_email` text,
  `cc_email` text,
  `created_time` datetime DEFAULT NULL,
  `email_body` text,
  `email_id` varchar(255) DEFAULT NULL,
  `email_title` text,
  `is_email_sent` tinyint(1) DEFAULT '0',
  `notification_type` varchar(255) DEFAULT NULL,
  `search_id` varchar(255) DEFAULT NULL,
  `sent_datetime` datetime DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `user_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`mail_messageid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.





-- Dumping structure for table mystudies_userregistration.participant_registry_site
CREATE TABLE IF NOT EXISTS `participant_registry_site` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NULL DEFAULT NULL,
  `created_by` int(20) DEFAULT '0',
  `email` varchar(255) DEFAULT NULL,
  `enrollment_token` varchar(50) DEFAULT NULL,
  `enrollment_token_expiry` timestamp NULL DEFAULT NULL,
  `invitation_date` timestamp NULL DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `onboarding_status` char(8) DEFAULT NULL,
  `site_id` int(11) DEFAULT NULL,
  `disabled_date` timestamp NULL DEFAULT NULL,
  `invitation_count` bigint(20) NOT NULL DEFAULT '0',
  `study_info_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrtseodvj7n9yjtwfqiixtviec` (`study_info_id`),
  KEY `FKa0f0un45iyajvjfqq6ok42lqc` (`site_id`),
  CONSTRAINT `FKa0f0un45iyajvjfqq6ok42lqc` FOREIGN KEY (`site_id`) REFERENCES `sites` (`id`),
  CONSTRAINT `FKrtseodvj7n9yjtwfqiixtviec` FOREIGN KEY (`study_info_id`) REFERENCES `study_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=169 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.participant_study_info
CREATE TABLE IF NOT EXISTS `participant_study_info` (
  `participant_study_info_id` int(11) NOT NULL,
  `adherence` int(11) DEFAULT NULL,
  `bookmark` tinyint(1) DEFAULT NULL,
  `completion` int(11) DEFAULT NULL,
  `consent_status` tinyint(1) DEFAULT NULL,
  `eligibility` tinyint(1) DEFAULT NULL,
  `enrolled_date` datetime DEFAULT NULL,
  `participant_id` varchar(255) DEFAULT NULL,
  `sharing` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `withdrawal_date` datetime DEFAULT NULL,
  `participant_registry_site_id` int(11) DEFAULT NULL,
  `site_id` int(11) DEFAULT NULL,
  `study_info_id` int(11) DEFAULT NULL,
  `user_details_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`participant_study_info_id`),
  UNIQUE KEY `UK_wic7o2oog14p35skw71ix3q0` (`participant_id`),
  KEY `FKb9362vga03lqkb0k46wsmi53x` (`participant_registry_site_id`),
  KEY `FKog8x3evjo4h227yc1jgtm2m4u` (`study_info_id`),
  KEY `FKodfgu8how5y9w4n048u2k4q79` (`user_details_id`),
  KEY `FKeppgsoyc8ldsx8mciwjo49j9u` (`site_id`),
  CONSTRAINT `FKb9362vga03lqkb0k46wsmi53x` FOREIGN KEY (`participant_registry_site_id`) REFERENCES `participant_registry_site` (`id`),
  CONSTRAINT `FKeppgsoyc8ldsx8mciwjo49j9u` FOREIGN KEY (`site_id`) REFERENCES `sites` (`id`),
  CONSTRAINT `FKodfgu8how5y9w4n048u2k4q79` FOREIGN KEY (`user_details_id`) REFERENCES `user_details` (`user_details_id`),
  CONSTRAINT `FKog8x3evjo4h227yc1jgtm2m4u` FOREIGN KEY (`study_info_id`) REFERENCES `study_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.sites
CREATE TABLE IF NOT EXISTS `sites` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int(20) DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `target_enrollment` int(11) DEFAULT '0',
  `location_id` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `modified_by` int(20) DEFAULT NULL,
  `modified_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FKbt0ribr7l9fnxpx549o2rrqy3` (`location_id`),
  KEY `FKii2p6mi7qcuwjl8613j0wam6` (`study_id`),
  CONSTRAINT `FKbt0ribr7l9fnxpx549o2rrqy3` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`),
  CONSTRAINT `FKii2p6mi7qcuwjl8613j0wam6` FOREIGN KEY (`study_id`) REFERENCES `study_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.sites_permissions
CREATE TABLE IF NOT EXISTS `sites_permissions` (
  `id` int(11) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int(20) DEFAULT '0',
  `edit` tinyint(1) DEFAULT '0',
  `app_info_id` int(11) DEFAULT NULL,
  `site_id` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `ur_admin_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtcdk83k4i02tjuags2cpu8acn` (`app_info_id`),
  KEY `FK4cuace13u7fwqshex5yrysb87` (`site_id`),
  KEY `FKpecdq337n7mvhy73p5by3d13e` (`study_id`),
  KEY `FK89o9erfie9bst1wa3dh01q90w` (`ur_admin_user_id`),
  CONSTRAINT `FK4cuace13u7fwqshex5yrysb87` FOREIGN KEY (`site_id`) REFERENCES `sites` (`id`),
  CONSTRAINT `FK89o9erfie9bst1wa3dh01q90w` FOREIGN KEY (`ur_admin_user_id`) REFERENCES `ur_admin_user` (`id`),
  CONSTRAINT `FKpecdq337n7mvhy73p5by3d13e` FOREIGN KEY (`study_id`) REFERENCES `study_info` (`id`),
  CONSTRAINT `FKtcdk83k4i02tjuags2cpu8acn` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`app_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.study_consent
CREATE TABLE IF NOT EXISTS `study_consent` (
  `study_consent_id` int(11) NOT NULL,
  `_ts` varchar(255) DEFAULT NULL,
  `pdf` varchar(255) DEFAULT NULL,
  `pdfpath` varchar(255) DEFAULT NULL,
  `pdf_storage` tinyint(4) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `study_info_id` int(11) DEFAULT NULL,
  `user_details_id` int(11) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  `participant_study_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`study_consent_id`),
  KEY `FK585y9moi8wkwmurn0w54ebl36` (`participant_study_id`),
  CONSTRAINT `FK585y9moi8wkwmurn0w54ebl36` FOREIGN KEY (`participant_study_id`) REFERENCES `participant_study_info` (`participant_study_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.study_info
CREATE TABLE IF NOT EXISTS `study_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `created_by` int(20) DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `custom_id` varchar(255) DEFAULT NULL,
  `description` longtext,
  `enrolling` varchar(255) DEFAULT NULL,
  `modified_by` int(20) DEFAULT NULL,
  `modified_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `name` varchar(255) DEFAULT NULL,
  `sponsor` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `tagline` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `app_info_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7q83jdpn6sguh4ly7fi8ahb7o` (`app_info_id`),
  CONSTRAINT `FK7q83jdpn6sguh4ly7fi8ahb7o` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`app_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.study_permissions
CREATE TABLE IF NOT EXISTS `study_permissions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` date DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `edit` tinyint(1) DEFAULT '0',
  `app_info_id` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `ur_admin_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9lgwsrqrpnx1pk2cfqdg7legi` (`app_info_id`),
  KEY `FK6j7ma1f3bq4j7v607fohqwj57` (`study_id`),
  KEY `FKnh034h5kkise2bj47vwne1ibf` (`ur_admin_user_id`),
  CONSTRAINT `FK6j7ma1f3bq4j7v607fohqwj57` FOREIGN KEY (`study_id`) REFERENCES `study_info` (`id`),
  CONSTRAINT `FK9lgwsrqrpnx1pk2cfqdg7legi` FOREIGN KEY (`app_info_id`) REFERENCES `app_info` (`app_info_id`),
  CONSTRAINT `FKnh034h5kkise2bj47vwne1ibf` FOREIGN KEY (`ur_admin_user_id`) REFERENCES `ur_admin_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.ur_admin_user
CREATE TABLE IF NOT EXISTS `ur_admin_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code_expire_date` datetime DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `created_by` int(20) DEFAULT '0',
  `email` varchar(100) DEFAULT NULL,
  `email_changed` tinyint(1) DEFAULT NULL,
  `email_code` varchar(255) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `manage_locations` tinyint(1) DEFAULT NULL,
  `manage_users` tinyint(1) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `ur_admin_auth_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.ur_admin_user_audit_log
CREATE TABLE IF NOT EXISTS `ur_admin_user_audit_log` (
  `ur_admin_user_audit_log_id` int(11) NOT NULL AUTO_INCREMENT,
  `ur_admin_users_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ur_admin_user_audit_log_id`),
  KEY `FK7qm17id61g7aqyw97y2nxbl57` (`ur_admin_users_id`),
  CONSTRAINT `FK7qm17id61g7aqyw97y2nxbl57` FOREIGN KEY (`ur_admin_users_id`) REFERENCES `ur_admin_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.user_app_details
CREATE TABLE IF NOT EXISTS `user_app_details` (
  `user_app_details_id` int(11) NOT NULL,
  `app_info_id` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `user_details_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_app_details_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.user_details
CREATE TABLE IF NOT EXISTS `user_details` (
  `user_details_id` int(11) NOT NULL,
  `_ts` datetime DEFAULT NULL,
  `app_info_id` int(11) DEFAULT NULL,
  `code_expire_date` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `email_code` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `local_notification_flag` tinyint(1) DEFAULT NULL,
  `locale` varchar(255) DEFAULT NULL,
  `reminder_lead_time` varchar(255) DEFAULT NULL,
  `remote_notification_flag` tinyint(1) DEFAULT NULL,
  `security_token` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `touch_id` tinyint(1) DEFAULT NULL,
  `use_pass_code` tinyint(1) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `verification_date` datetime DEFAULT NULL,
  PRIMARY KEY (`user_details_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `personalized_user_report` (
  `id` INT NOT NULL AUTO_INCREMENT, 
  `activity_date_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
  `report_content` TEXT, 
  `report_title` varchar(255), 
  `study_info_id` INT, 
  `user_id` INTEGER, 
  PRIMARY KEY (`id`),
  FOREIGN KEY (study_info_id) REFERENCES study_info(id),
  FOREIGN KEY (user_id) REFERENCES user_details(user_details_id)
);

-- Data exporting was unselected.

-- Dumping structure for table mystudies_userregistration.user_institution
CREATE TABLE IF NOT EXISTS `user_institution` (
  `user_institution_id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_details_id` int(11) NOT NULL UNIQUE,
  `institution_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_institution_id`),
  FOREIGN KEY (user_details_id) REFERENCES user_details(user_details_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
