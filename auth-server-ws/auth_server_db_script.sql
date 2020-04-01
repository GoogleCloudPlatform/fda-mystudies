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


-- Dumping database structure for auth_server
CREATE DATABASE IF NOT EXISTS `auth_server` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `auth_server`;

-- Dumping structure for table auth_server.activity_log
CREATE TABLE IF NOT EXISTS `activity_log` (
  `activity_log_id` int(11) NOT NULL,
  `activity_date_time` datetime DEFAULT NULL,
  `actvity_name` varchar(255) DEFAULT NULL,
  `activity_description` varchar(255) DEFAULT NULL,
  `auth_user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`activity_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table auth_server.client_info
CREATE TABLE IF NOT EXISTS `client_info` (
  `client_info_id` int(11) NOT NULL,
  `app_code` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `secret_key` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`client_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table auth_server.hibernate_sequence
CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Insert 0 into hibernate_sequence if the table is empty.
INSERT INTO auth_server.hibernate_sequence (next_val)
SELECT 0
WHERE NOT EXISTS (SELECT * FROM auth_server.hibernate_sequence);

-- Data exporting was unselected.

-- Dumping structure for table auth_server.login_attempts
CREATE TABLE IF NOT EXISTS `login_attempts` (
  `id` int(11) NOT NULL,
  `no_of_attempts` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  `appl_mode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table auth_server.sessions
CREATE TABLE IF NOT EXISTS `sessions` (
  `id` int(11) NOT NULL,
  `access_token` varchar(255) DEFAULT NULL,
  `client_token` varchar(255) DEFAULT NULL,
  `expire_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `refresh_token` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table auth_server.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL,
  `account_status` varchar(255) DEFAULT NULL,
  `app_code` varchar(255) DEFAULT NULL,
  `app_id` varchar(255) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `email` varchar(255) DEFAULT NULL,
  `email_verification_status` varchar(255) DEFAULT NULL,
  `org_id` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `password_updated_date` datetime DEFAULT NULL,
  `reminder_lead_time` varchar(255) DEFAULT NULL,
  `reset_password` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `temp_password` tinyint(1) DEFAULT NULL,
  `temp_password_date` datetime DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `password_expire_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table auth_server.users_password_history
CREATE TABLE IF NOT EXISTS `users_password_history` (
  `password_history_id` int(11) NOT NULL,
  `_ts` datetime DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`password_history_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
