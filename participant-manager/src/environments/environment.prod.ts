// Replace the example domain name with your deployed address.
export const environment = {
  production: true,
  // remove http/https to appear relative. xsrf-token skips absolute paths.
  participantManagerDatastoreUrl:
    '//participants-btc-qa.boston-technology.com/participant-manager-datastore',
  baseHref: '/participant-manager/',
  hydraLoginUrl:
    'https://participants-btc-qa.boston-technology.com/oauth2/auth',
  authServerUrl:
    'https://participants-btc-qa.boston-technology.com/auth-server',
  authServerRedirectUrl:
    'https://participants-btc-qa.boston-technology.com/auth-server/callback',
  hydraClientId: 'yvGGhtvbgiDCB2z9',
  appVersion: 'v0.1',
  termsPageTitle: 'Terms title goes here',
  termsPageDescription: 'Terms description goes here',
  aboutPageTitle: 'About page title goes here',
  aboutPageDescription: 'About page description goes here',
  copyright: 'Copyright 2020-2021 Google LLC.',
};
