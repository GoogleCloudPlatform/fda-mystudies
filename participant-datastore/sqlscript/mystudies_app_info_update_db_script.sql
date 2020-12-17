
/*
before running this script make sure you have completed the below points
1.Study should be published
2.Mobile App should be ready

Notes:
1.Please set the values which you want to update 
2.set @custom_app_id to column custom_app_id's value of app_info table for which you want to update 
*/
SET @android_bundle_id := ''; /*This is the value of applicationId residing in Android/app/build.gradle*/
SET @android_server_key := ''; /*This is the Firebase Cloud Messaging Server key*/
SET @app_description := '';  /*Description about the app.*/
SET @app_name := ''; /*Name of the app*/
SET @ios_bundle_id := '';     /*Get the bundle id using Xcode: Project target > General tab > Identity section > Bundle identifier*/
SET @ios_certificate := '';   /*Base64 converted p12 file*/
SET @ios_certificate_password := '';   /*If the p12 file is encripted then the password used to encript this file has to be passed as value for iOS certificate password.*/

SET @custom_app_id := '';
UPDATE mystudies_participant_datastore.app_info a 
   SET
a.android_bundle_id=CASE WHEN @android_bundle_id<>'' THEN @android_bundle_id ELSE a.android_bundle_id END , 
a.android_server_key= CASE WHEN @android_server_key<>'' THEN @android_server_key ELSE a.android_server_key END ,
a.app_description= CASE WHEN @app_description<> '' THEN @app_description ELSE a.app_description END ,
a.app_name= CASE WHEN @app_name<> '' THEN @app_name ELSE a.app_name END ,
a.ios_bundle_id= CASE WHEN @ios_bundle_id<> '' THEN @ios_bundle_id ELSE a.ios_bundle_id END ,
a.ios_certificate= CASE WHEN @ios_certificate<> '' THEN @ios_certificate  ELSE a.ios_certificate END ,
a.ios_certificate_password= CASE WHEN @ios_certificate_password<> '' THEN @ios_certificate_password ELSE a.ios_certificate_password END
WHERE  a.custom_app_id=@custom_app_id 