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
  `app_id` varchar(100) NOT NULL,
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

/*
-- Query: SELECT * FROM oauth_scim_db.users
LIMIT 0, 1000

-- Date: 2020-10-05 15:18

	"email":"superadmin@gmail.com",
	"password":"Ch@ngeM3",
	"appId":"PARTICIPANT MANAGER",
	"status":0
*/
INSERT INTO `users` (`id`,`app_id`,`created`,`email`,`status`,`temp_reg_id`,`user_id`,`user_info`) VALUES ('8ad16a8c74f823a10174f82c9a300001','PARTICIPANT MANAGER','2020-10-05 15:21:46','superadmin@gmail.com',0,'bd676334dd745c6afaa6547f9736a4c4df411a3ca2c4f514070daae31008cd9d','96494ebc2ae5ac344437ec19bfc0b09267a876015b277e1f6e9bfc871f578508','{\"password\": {\"hash\": \"9bb85ab372ccd4b69b78477a89ddd8437d26e0fe10d2618e1edf48cddf56f1d2fcf9de71a39cdae01493c69e2bbc1b3ff890eda31ee2f4c00967e17f8fe03556\", \"salt\": \"1e73c28e50e41f2d2175ba3ba3349395ebe80c42f837ffaaa06a7adf170bd3238fbda39cb6357b8410aeafcd8647619abfd2657900bd26011c7775504760c968\", \"expire_timestamp\": 1609667506735}, \"password_history\": [{\"hash\": \"9bb85ab372ccd4b69b78477a89ddd8437d26e0fe10d2618e1edf48cddf56f1d2fcf9de71a39cdae01493c69e2bbc1b3ff890eda31ee2f4c00967e17f8fe03556\", \"salt\": \"1e73c28e50e41f2d2175ba3ba3349395ebe80c42f837ffaaa06a7adf170bd3238fbda39cb6357b8410aeafcd8647619abfd2657900bd26011c7775504760c968\", \"expire_timestamp\": 1609667506735}]}');

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
