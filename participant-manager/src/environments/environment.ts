// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
export const environment = {
  production: false,
  participantManagerDatastoreUrl:
    'https://35.193.185.224:8093/participant-manager-datastore',
  hydraLoginUrl: 'https://35.193.185.224:9000/oauth2/auth',
  authServerUrl: 'http://35.193.185.224:8087/auth-server',
  authServerRedirectUrl: 'https://34.69.210.52/qa/auth-server/callback',
  hydraClientId: 'MYSTUDIES_OAUTH_CLIENT',
  appVersion: 'v0.1',

  // Branding centralisation
  aboutPageTitle: 'about page title goes here',
  aboutPageDescription: 'about page description goes here',
  termsPageTitle: 'terms title goes here',
  termsPageDescription: 'terms description goes here',
  copyright: 'Copyright 2020 Google LLC.',
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
