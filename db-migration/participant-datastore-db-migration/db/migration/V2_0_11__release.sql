
/* Column added for integrating Google Identity Platform with MyStudies */

ALTER TABLE `mystudies_participant_datastore`.`ur_admin_user` ADD `idp_user` CHAR(1)  DEFAULT 'N';

UPDATE `mystudies_participant_datastore`.`ur_admin_user` set `idp_user`='N' where `idp_user` IS NULL;

ALTER TABLE `mystudies_participant_datastore`.`ur_admin_user` ADD `phone_number` VARCHAR(16);