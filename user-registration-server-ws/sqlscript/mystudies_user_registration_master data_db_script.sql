/*

Notes:
1.Please replace actual_value with the value which you want to save
*/

SET @orgName :='actual_value';
SET @orgCustomId :='actual_value';

INSERT INTO `mystudies_userregistration`.`org_info` (`name`, `org_id`) VALUES (@orgName, @orgCustomId);