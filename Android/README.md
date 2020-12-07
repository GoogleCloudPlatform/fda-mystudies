<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

# Overview
This directory contains all the code necessary to build the **FDA MyStudies** Android application for study participants. Customization of the [`build.gradle`](app/build.gradle), [`api.properties`](api.properties) and [`strings.xml`](app/src/fda/res/values/strings.xml) files will enable your Android application to interact with the other components of your **FDA MyStudies** deployment. Further customization of app branding can be accomplished by replacing the default application images with your own. All configuration related to the creation and operation of studies is done using the [`Study builder`](../study-builder/) without need for code changes or redeployment of the mobile application.

<!--TODO A demonstration of the Android mobile application can be found [here](todo). --->

![Example screens](../documentation/images/mobile-screens.png "Example screens")

# Requirements
The **FDA MyStudies** Android application requires [Android Studio](https://developer.android.com/studio/index.html) and can be run on Android versions starting from Kitkat.

# Platform integration
The **FDA MyStudies** mobile application fetches all study, schedule, activity, eligibility, consent and notification information from the [`Study datastore`](../study-datastore/) and posts pseudonymized participant response data to the [`Response datastore`](../response-datastore/). Consent forms and any other identifiable data is posted to the [`Participant datastore`](../participant-datastore/). Email and password authentication is handled by the MyStudies [`Auth server`](../auth-server/) using OAuth 2.0 and [`Hydra`](/hydra/).

# Configuration instructions

1. Set the `applicationId` in [`Android/app/build.gradle`](app/build.gradle) to your [Application ID](https://developer.android.com/studio/build/application-id)
1. Modify [`Android/api.properties`](api.properties) to match the configuration of your backend services
1. Update the following in the [`Android/app/src/fda/res/values/strings.xml`](app/src/fda/res/values/strings.xml) file:
    -    Set `deeplink_host` to redirect to the app from the [`Hydra`](/hydra/) auth server (for example, `app://mystudies.<your-domain>/mystudies` - more information about deep links within Android applications is located [here](https://developer.android.com/training/app-links/deep-linking))
    -    Set `google_maps_key` to the API key obtained following the instructions located [here](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
    -    Set `package_name` and `app_name` to correspond to a value you define for `applicationId` in [`Android/app/build.gradle`](app/build.gradle) ([details](https://developer.android.com/studio/build/application-id)) 
    -    Customize user-facing text strings as necessary
1. Configure push notifications
    -    Go to the [Firebase console](https://console.firebase.google.com/) and select the project you configured for Cloud Firestore during [`Response datastore`](/response-datastore/) deployment 
    -    [Register your Android app](https://firebase.google.com/docs/android/setup) in the Cloud Messaging section of the Firebase console (the `Android package name` is the `applicationID` value in the [`Android/app/build.gradle`](app/build.gradle) file)
    -    Download the `google-services.json` file from the [Firebase project settings](https://console.firebase.google.com/project/_/settings/general/) page and replace [`Android/app/src/fda/google-services.json`](app/src/fda/google-services.json)
    -    Set the `android_server_key` field of [`participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql`](../participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql) to the `Server key` retrieved from the [Firebase project settings](https://console.firebase.google.com/project/_/settings/general/) page
    -    Run the updated [`mystudies_app_info_update_db_script.sql`](../participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql) script on the `mystudies_participant_datastore` database that you created during [`Participant datastore`](/participant-datastore/) deployment  ([instructions](https://cloud.google.com/sql/docs/mysql/import-export/importing#importing_a_sql_dump_file))
1. *Optional.* Customize images and text
     -    Replace images at the appropriate resolution in the [`Android/app/src/fda/res/`](app/src/fda/res/) directories: `mipmap-hdpi`, `mipmap-mdpi`, `mipmap-xhdpi`, `mipmap-xxhdpi`, `mipmap-xxxhdpi`, `drawable-560dpi`, `drawable-xhdpi`, `drawable-xxhdpi`, `drawable-xxxhdpi`
     -    Customize user-facing text in the [`Android/app/src/main/res/values/strings.xml`](app/src/main/res/values/strings.xml) file 
1. Open the [`Android/`](../Android/) directory that contains your modifications as an existing project in [Android Studio](https://developer.android.com/studio/index.html)
1. If necessary, install the Android 10 SDK using Tools &rarr; [SDK Manager](https://developer.android.com/studio/intro/update#sdk-manager), then File &rarr; Sync Project with Gradle Files (do not update Gradle plugin)

# Building and deploying

To build and run your **FDA MyStudies** application, follow the instructions [here](https://developer.android.com/studio/run).

To distribute your application to users, review the options [here](https://developer.android.com/studio/publish). 

***
<p align="center">Copyright 2020 Google LLC</p>
