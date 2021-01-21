-- Dumping structure for table oauth_server_hydra.users
DROP TABLE IF EXISTS `users_migration`;
CREATE TABLE IF NOT EXISTS `users_migration` (
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