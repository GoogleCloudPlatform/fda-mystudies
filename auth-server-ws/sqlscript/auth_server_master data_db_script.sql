/*
before running this script make sure you have completed the below points
1. initialize clientId and secretkey value in property/config file of respective project with the value specified here

Notes:
1.Please replace actual_value with the value which you want to save
*/
SET @mobileAppClientId := 'actual_value';
SET @mobileAppSecretKey := 'actual_value';

SET @URWebAppWSClientId := 'actual_value';
SET @URWebAppWSSecretKey := 'actual_value';

SET @UserRegServerClientId := 'actual_value';
SET @UserRegServerSecretKey := 'actual_value';

SET @ResponseServerClientId := 'actual_value';
SET @ResponseServerSecretKey := 'actual_value';

SET @WCPAppClientId := 'actual_value';
SET @WCPSecretKey := 'actual_value';


INSERT INTO `auth_server`.`client_info` ( `app_code`, `client_id`, `secret_key`) VALUES ( 'MA', @mobileAppClientId, @mobileAppSecretKey);
INSERT INTO `auth_server`.`client_info` ( `app_code`, `client_id`, `secret_key`) VALUES ( 'USWS', @URWebAppWSClientId, @URWebAppWSSecretKey);
INSERT INTO `auth_server`.`client_info` ( `app_code`, `client_id`, `secret_key`) VALUES ( 'URS', @UserRegServerClientId, @UserRegServerSecretKey);
INSERT INTO `auth_server`.`client_info` ( `app_code`, `client_id`, `secret_key`) VALUES ( 'RS', @ResponseServerClientId, @ResponseServerSecretKey);
INSERT INTO `auth_server`.`client_info` ( `app_code`, `client_id`, `secret_key`) VALUES ( 'WCP', @WCPAppClientId, @WCPSecretKey);fda_hphcclient_info