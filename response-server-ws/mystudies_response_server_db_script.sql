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


-- Dumping database structure for mystudies_response_server
CREATE DATABASE IF NOT EXISTS `mystudies_response_server` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
USE `mystudies_response_server`;

-- Dumping structure for table mystudies_response_server.activity_log
CREATE TABLE IF NOT EXISTS `activity_log` (
  `activity_log_id` int(11) NOT NULL,
  `activity_date_time` datetime DEFAULT NULL,
  `actvity_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `activity_description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `auth_user_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `server_client_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`activity_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_response_server.hibernate_sequence
CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Insert 0 into hibernate_sequence if the table is empty.
INSERT INTO mystudies_response_server.hibernate_sequence (next_val)
SELECT 0
WHERE NOT EXISTS (SELECT * FROM mystudies_response_server.hibernate_sequence);

-- Data exporting was unselected.

-- Dumping structure for table mystudies_response_server.participant_activities
CREATE TABLE IF NOT EXISTS `participant_activities` (
  `id` int(11) NOT NULL,
  `activity_complete_id` int(11) DEFAULT NULL,
  `activity_end_date` datetime DEFAULT NULL,
  `activity_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `activity_run_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `activity_start_date` datetime DEFAULT NULL,
  `activity_state` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `activity_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `activity_version` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `anchordate_created_date` datetime DEFAULT NULL,
  `anchordate_version` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `bookmark` tinyint(1) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `missed` int(11) DEFAULT NULL,
  `participant_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `study_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `total` int(11) DEFAULT NULL,
  `_ts` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  `participant_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtj4iift69j6es8i24xyl3gr6x` (`participant_id`),
  CONSTRAINT `FKtj4iift69j6es8i24xyl3gr6x` FOREIGN KEY (`participant_id`) REFERENCES `participant_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_response_server.participant_info
CREATE TABLE IF NOT EXISTS `participant_info` (
  `id` int(11) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `participant_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `study_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `token_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_aoxsbtvee1bx8coxxdc1d9kv0` (`participant_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
