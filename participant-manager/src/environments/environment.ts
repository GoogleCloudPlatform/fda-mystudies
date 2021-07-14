// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

// export const environment = {
//   production: false,
//   participantManagerDatastoreUrl:
//     'http://35.222.67.4:8081/participant-manager-datastore',
//   baseHref: '/',
//   hydraLoginUrl: 'https://35.222.67.4:9000/oauth2/auth',
//   authServerUrl: 'http://35.222.67.4:8084/auth-server',
//   authServerRedirectUrl: 'https://34.69.210.52/dev/auth-server/callback',
//   hydraClientId: 'MYSTUDIES_OAUTH_CLIENT',
//   appVersion: '',
//   termsPageTitle: 'Terms title goes here',
//   termsPageDescription: 'Terms description goes here',
//   aboutPageTitle: 'About page title goes here',
//   aboutPageDescription: 'About page description goes here',
//   copyright: 'Copyright 2020-2021 Google LLC.',
// };

export const environment = {
  participantManagerDatastoreUrl:
    '//participants.btcsoft-dev.boston-technology.com/participant-manager-datastore',
  baseHref: '/participant-manager/',
  hydraLoginUrl:
    'https://participants.btcsoft-dev.boston-technology.com/oauth2/auth',
  authServerUrl:
    'https://participants.btcsoft-dev.boston-technology.com/auth-server',
  authServerRedirectUrl:
    'https://participants.btcsoft-dev.boston-technology.com/auth-server/callback',
  hydraClientId: 'TUUo9PjRQN80y3Mt',
  appVersion: 'v0.1',
  termsPageTitle: 'Terms title goes here',
  termsPageDescription: 'Terms description goes here',
  aboutPageTitle: 'About page title goes here',
  aboutPageDescription: 'About page description goes here',
  copyright: 'Copyright 2020-2021 Google LLC.',
  production: false,
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
