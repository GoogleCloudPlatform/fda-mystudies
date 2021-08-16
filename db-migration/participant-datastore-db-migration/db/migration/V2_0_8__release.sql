/* ISSUE #616 Use FCM instead of APNS for iOS push notifications */
ALTER TABLE mystudies_participant_datastore.app_info ADD ios_server_key VARCHAR(255) DEFAULT NULL;