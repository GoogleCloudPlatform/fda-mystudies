// Replace the example domain name with your deployed address.
export const environment = {
  production: true,
  // remove http/https to appear relative. xsrf-token skips absolute paths.
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
};
