
/*
This script is intended to be run on the `Participant datastore` database each time a new set of mobile applications
are added to your MyStudies deployment. For example, once you have decided what the value of your 'App ID' is going
to be (configured on the Settings page of the Study Builder UI), and you have determined the bundle information for the
corresponding mobile applications (iOS and/or Android -- see their respective README files), you can update this script
with those values. After running this script, your `Participant datastore` will know how to interact with those mobile
applications. 
*/

/*
Configure the values below. If you are do not have an Android or iOS application, leave those fields empty.
*/

/* General configuration */
SET @custom_app_id := '';                /* The `App ID` that you configured on the Settings page of the Study Builder UI */
SET @app_name := '';                     /* A name for the application. Used to identify the application in messages to admins and participants. */
SET @app_description := '';              /* A brief description of this application. Used to describe the application in messages to admins. */


/* Android configuration */
SET @android_bundle_id := '';            /* This is the value of `applicationId` that you configured in Android/app/build.gradle during Android configuration */
SET @android_server_key := '';           /* This is the Firebase Cloud Messaging server key that you obtained during Android configuration */

/* iOS configuration */
SET @ios_bundle_id := '';                /* Obtain this value using Xcode: Project target > General tab > Identity section > Bundle identifier */
SET @ios_certificate := '';              /* This is the Base64 converted p12 file that you obtained during iOS configuration */
SET @ios_certificate_password := '';     /* This is the password for the p12 certificate (necessary if your certificate is encrypted - otherwise leave empty) */

/* No need to modify below this line */
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
