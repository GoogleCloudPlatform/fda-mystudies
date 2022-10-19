<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

# Overview
This directory contains all the code necessary to build the **FDA MyStudies** Android application for study participants. Customization of the [`build.gradle`](app/build.gradle), [`api.properties`](api.properties) and [`strings.xml`](app/src/fda/res/values/strings.xml) files will enable your Android application to interact with the other components of your **FDA MyStudies** deployment. Further customization of app branding can be accomplished by replacing the default application images with your own. All configuration related to the creation and operation of studies is done using the [`Study builder`](../study-builder/) without need for code changes or redeployment of the mobile application.

![Example screens](../documentation/images/mobile-screens.png "Example screens")

# Requirements
The **FDA MyStudies** Android application requires [Android Studio](https://developer.android.com/studio/index.html) and can be run on Android versions starting from Kitkat.

# Platform integration
The **FDA MyStudies** mobile application fetches all study, schedule, activity, eligibility, consent and notification information from the [`Study datastore`](../study-datastore/) and posts pseudonymized participant response data to the [`Response datastore`](../response-datastore/). Consent forms and any other identifiable data is posted to the [`Participant datastore`](../participant-datastore/). Email and password authentication is handled by the MyStudies [`Auth server`](../auth-server/) using OAuth 2.0 and [`Hydra`](/hydra/).

# Configuration instructions

> Note: Be cautious about making changes to `build.gradle` or other files in the `/Android/` directory unless those changes are specifically mentioned in the steps below. It is recommended that file editing is done outside of Android Studio, as Android Studio can introduce conflicting configuration files (for example, avoid updating Grade plugin if prompted).

1. Set the `applicationId` in [`Android/app/build.gradle`](app/build.gradle) to your [Application ID](https://developer.android.com/studio/build/application-id) (this will match the value of your `manual-android-bundle-id` secret when following the semi-automated [deployment guide](/deployment/README.md))
1. Modify [`Android/api.properties`](api.properties) to match the configuration of your backend services
1. Update the following in the [`Android/app/src/fda/res/values/strings.xml`](app/src/fda/res/values/strings.xml) file:
    -    Set `deeplink_host` to define the [deep link URL](https://developer.android.com/training/app-links/deep-linking) that will be used for redirects (for example, if you set `deeplink_host` to `<subdomain>.<domain>` your deeplink would be `app://<subdomain>.<domain>/mystudies`) 
    -    Set `google_maps_key` to the API key obtained following the instructions located [here](https://developers.google.com/maps/documentation/android-sdk/get-api-key) (you do not need to follow steps from the API key documentation about updating project or application files)
    -    Set `package_name` to the value of [`applicationId`](https://developer.android.com/studio/build/application-id) in [`Android/app/build.gradle`](app/build.gradle)
    -    Set `app_name` to the application name that will be shown to users 
    -    Customize user-facing text strings as necessary
1. Configure push notifications
    -    Go to the [Firebase console](https://console.firebase.google.com/) and select the project you configured for Cloud Firestore during [`Response datastore`](/response-datastore/) deployment 
    -    [Register your Android app](https://firebase.google.com/docs/android/setup) in the Cloud Messaging section of the Firebase console (the `Android package name` is the `applicationID` value in the [`Android/app/build.gradle`](app/build.gradle) file)
    -    Download the `google-services.json` file from the [Firebase project settings](https://console.firebase.google.com/project/_/settings/general/) page and replace [`Android/app/src/fda/google-services.json`](app/src/fda/google-services.json) (you do not need to update either of your `build.gradle` files, even if prompted)
    -    To enable Google Analytics, go to the Firebase console, click on the dashboard under Analytics section from the side menu and click on enable Google Analytics button to view the events triggered from the mobile
1. Configure your [`Participant datastore`](/participant-datastore/) instance to interface with your mobile application (skip this step if following the semi-automated [deployment guide](/deployment/README.md) - you will complete an automated version of this task when you return to that guide)
    -    Make a copy of the [`mystudies_participant_datastore_db_script.sql`](../participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql) and update the values to match your Android configuration
    -   Optionally, configure the iOS fields to match your iOS configuration (not necessary if you are not configuring an iOS application)
    -    Run your updated [`mystudies_app_info_update_db_script.sql`](../participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql) script on the `mystudies_participant_datastore` database that you created during [`Participant datastore`](/participant-datastore/) deployment  ([instructions](https://cloud.google.com/sql/docs/mysql/import-export/importing#importing_a_sql_dump_file))
1. *Optional.* Customize images and text
     -    Replace images at the appropriate resolution in the [`Android/app/src/fda/res/`](app/src/fda/res/) directories: `mipmap-hdpi`, `mipmap-mdpi`, `mipmap-xhdpi`, `mipmap-xxhdpi`, `mipmap-xxxhdpi`, `drawable-560dpi`, `drawable-xhdpi`, `drawable-xxhdpi`, `drawable-xxxhdpi`
     -    Customize user-facing text in the [`Android/app/src/main/res/values/strings.xml`](app/src/main/res/values/strings.xml) file 
1. Open the [`Android/`](../Android/) directory that contains your modifications as an existing project in [Android Studio](https://developer.android.com/studio/index.html)
1. Install the Android 10 SDK using [SDK Manager](https://developer.android.com/studio/intro/update#sdk-manager), then `Sync Project with Gradle Files` (do not update Gradle plugin)	

# Building and deploying

To build and run your **FDA MyStudies** application, follow the instructions [here](https://developer.android.com/studio/run).

To distribute your application to users, review the options [here](https://developer.android.com/studio/publish). 

***
<p align="center">Copyright 2020 Google LLC</p>
