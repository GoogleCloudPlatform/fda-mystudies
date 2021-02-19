// Replace the example domain name with your deployed address.
export const environment = {
  production: true,
  // remove http/https to appear relative. xsrf-token skips absolute paths.
  participantManagerDatastoreUrl: '//<BASE_URL>/participant-manager-datastore',
  baseHref: '/participant-manager/',
  hydraLoginUrl: 'https://<BASE_URL>/oauth2/auth',
  authServerUrl: 'https://<BASE_URL>/auth-server',
  authServerRedirectUrl: 'https://<BASE_URL>/auth-server/callback',
  hydraClientId: '<AUTH_SERVER_CLIENT_ID>',
  appVersion: 'v0.1',
  termsPageTitle: 'Terms title goes here',
  termsPageDescription: 'Terms description goes here',
  aboutPageTitle: 'About page title goes here',
  aboutPageDescription: 'About page description goes here',
  copyright: 'Copyright 2020-2021 Google LLC.',
};