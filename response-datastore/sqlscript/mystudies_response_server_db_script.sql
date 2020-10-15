-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.20 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for mystudies_response_server
DROP DATABASE IF EXISTS `mystudies_response_server`;
CREATE DATABASE IF NOT EXISTS `mystudies_response_server` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `mystudies_response_server`;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_response_server.participant_activities
DROP TABLE IF EXISTS `participant_activities`;
CREATE TABLE IF NOT EXISTS `participant_activities` (
  `id` varchar(255) NOT NULL,
  `activity_id` varchar(64) NOT NULL,
  `activity_run_id` varchar(32) NOT NULL,
  `activity_state` varchar(255) NOT NULL,
  `activity_version` varchar(32) NOT NULL,
  `bookmark` tinyint(1) DEFAULT '0',
  `completed_count` int DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `missed_count` int DEFAULT NULL,
  `participant_id` varchar(64) NOT NULL,
  `study_id` varchar(32) NOT NULL,
  `total_count` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `participant_activities_participant_id_idx` (`participant_id`),
  KEY `participant_activities_study_id_idx` (`study_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table mystudies_response_server.participant_info
DROP TABLE IF EXISTS `participant_info`;
CREATE TABLE IF NOT EXISTS `participant_info` (
  `id` varchar(255) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `participant_id` varchar(64) NOT NULL,
  `study_id` varchar(32) NOT NULL,
  `token_id` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ml7r3rylmf21yvdtoe1442mg6` (`participant_id`),
  KEY `participant_info_token_id_participant_id_idx` (`token_id`,`participant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
