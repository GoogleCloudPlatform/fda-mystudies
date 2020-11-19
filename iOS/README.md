![Build iOS](https://github.com/GoogleCloudPlatform/fda-mystudies/workflows/Build%20iOS/badge.svg) 
![SwiftLint](https://github.com/GoogleCloudPlatform/fda-mystudies/workflows/SwiftLint/badge.svg)

# Requirements
FDA MyStudies iOS app requires Xcode 11 or newer and can be run on iOS 11 and above.

# Backend Server Integration
FDA MyStudies Mobile Apps fetch all the Studies, Activities, Consent and Resources from Study Builder and datastores, and post responses provided by users to the Response datastore. In order for this app to communicate with the appropriate underlying services, you need to set up the following services and provide the appropriate URLs and credentials via app's configuration file:

#### Partcipant Datastore
Partcipant Datastore stores participants' information, their consent status and consent document once provided.

#### Study Builder
Study Builder provides the platform to create study, activities, consent, and resources.

#### Response Datastore
Response Datastore stores users’ responses to all study activities.

#### Auth Server
Handles authentication of the App with different backends.

#### Creating Build Configuration Files
1. To create a build configuration file, open MyStudies.xcworkspace, select the “File > New File…” menu item (⌘n), scroll down to the section labeled “Other”, and select the Configuration Settings File template. Next, save it somewhere in your project directory, making sure to add it to your desired targets.
2. Once you’ve created an xcconfig file, you can assign it to one or more build configurations for their associated targets.
3. Go to project info settings.
4. Under build configuration, select the configuration settings file for project(not for any target) you created in Step 1.

