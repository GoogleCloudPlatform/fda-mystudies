
/* Column added for integrating Google Identity Platform with MyStudies */

ALTER TABLE `oauth_server_hydra`.`users` ADD `idp_user` CHAR(1)  DEFAULT 'N';

UPDATE `oauth_server_hydra`.`users` set `idp_user`='N' where `idp_user` IS NULL;

ALTER TABLE `oauth_server_hydra`.`users` ADD `phone_number` VARCHAR(16);
