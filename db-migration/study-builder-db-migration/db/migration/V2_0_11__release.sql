
/* Column added for integrating Google Identity Platform with MyStudies */

ALTER TABLE `fda_hphc`.`users` ADD `idp_user` CHAR(1)  DEFAULT 'N';

UPDATE `fda_hphc`.`users` set `idp_user`='N' where `idp_user` IS NULL;
