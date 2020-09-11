
/*
before running this script make sure you have completed the below points
1.Study should be published
2.Mobile App should be ready

Notes:
1.Please set the values which you want to update 
2.leave the value empty/0 if you dont want to update it
3.set @app_info_id to primary key of row which you want to update 
*/
SET @android_bundle_id := 'w';
SET @android_server_key := '';
SET @app_description := '';
SET @custom_app_id := '';
SET @app_name := '';
SET @forgot_email_body := '';
SET @forgot_email_sub := '';
SET @from_email_id := '';
SET @from_email_password := '';
SET @ios_bundle_id := '';
SET @ios_certificate := '';
SET @ios_certificate_password := '';
SET @reg_email_body := '';
SET @reg_email_sub := '';

SET @app_info_id := 10;
UPDATE mystudies_userregistration.app_info a 
   SET
a.android_bundle_id=CASE WHEN @android_bundle_id<>'' THEN @android_bundle_id ELSE a.android_bundle_id END , 
a.android_server_key= CASE WHEN @android_server_key<>'' THEN @android_server_key ELSE a.android_server_key END ,
a.app_description= CASE WHEN @app_description<> '' THEN @app_description ELSE a.app_description END ,
a.custom_app_id= CASE WHEN @custom_app_id<> '' THEN @custom_app_id ELSE a.custom_app_id END ,
a.app_name= CASE WHEN @app_name<> '' THEN @app_name ELSE a.app_name END ,
a.forgot_email_body= CASE WHEN @forgot_email_body<> '' THEN @forgot_email_body ELSE a.forgot_email_body END ,
a.forgot_email_sub= CASE WHEN @forgot_email_sub<> '' THEN @forgot_email_sub ELSE a.forgot_email_sub END ,
a.from_email_id= CASE WHEN @from_email_id<> '' THEN @from_email_id ELSE a.from_email_id END ,
a.from_email_password= CASE WHEN @from_email_password<> '' THEN @from_email_password ELSE a.from_email_password END,
a.ios_bundle_id= CASE WHEN @ios_bundle_id<> '' THEN @ios_bundle_id ELSE a.ios_bundle_id END ,
a.ios_certificate= CASE WHEN @ios_certificate<> '' THEN @ios_certificate  ELSE a.ios_certificate END ,
a.ios_certificate_password= CASE WHEN @ios_certificate_password<> '' THEN @ios_certificate_password ELSE a.ios_certificate_password END ,
a.reg_email_body= CASE WHEN @reg_email_body<> '' THEN @reg_email_body ELSE a.reg_email_body END ,
a.reg_email_sub= CASE WHEN @reg_email_sub<> '' THEN @reg_email_sub ELSE a.reg_email_sub END

WHERE  a.app_info_id=@app_info_id