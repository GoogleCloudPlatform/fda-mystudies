# Requirements
FDA MyStudies Android app requires Android Studio and can be run on Android versions starting from Kitkat.

# Backend Server Integration
FDA MyStudies Mobile Apps fetch all the Studies, Activities, Consent and Resources from Study Builder and datastores, and post responses provided by users to the Response datastore. In order for this app to communicate with the appropriate underlying services, you need to set up the following services and provide the appropriate URLs and credentials in [api.properties](./api.properties):

#### Partcipant Datastore
Partcipant Datastore stores participants' information, their consent status and consent document once provided.

#### Study Builder
Study Builder provides the platform to create study, activities, consent, and resources.

#### Response Datastore
Response Datastore stores users’ responses to all study activities.

#### Auth Server
Handles authentication of the App with different backends.




