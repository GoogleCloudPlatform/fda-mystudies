-- --------------------------------------------------------
-- Host:                         35.196.150.7
-- Server version:               5.7.25-google - (Google)
-- Server OS:                    Linux
-- HeidiSQL Version:             11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for oauth_server_hydra
DROP DATABASE IF EXISTS `oauth_server_hydra`;
CREATE DATABASE IF NOT EXISTS `oauth_server_hydra` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `oauth_server_hydra`;

-- Dumping structure for table oauth_server_hydra.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` varchar(255) NOT NULL,
  `app_id` varchar(64) NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(320) NOT NULL,
  `status` int(11) NOT NULL,
  `temp_reg_id` varchar(64) DEFAULT NULL,
  `user_id` varchar(64) DEFAULT NULL,
  `user_info` json NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_index` (`user_id`),
  UNIQUE KEY `temp_reg_id_index` (`temp_reg_id`),
  KEY `users_app_id_email_idx` (`app_id`,`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
