// Replace the example domain name with your deployed address.
export const environment = {
  production: true,
  // remove http/https to appear relative. xsrf-token skips absolute paths.
  participantManagerDatastoreUrl:
    '//fda-mystudies.domain.com/participant-manager-datastore',
  baseHref: '/participant-manager/',
  hydraLoginUrl: 'https://fda-mystudies.domain.com/oauth2/auth',
  authServerUrl: 'https://fda-mystudies.domain.com/auth-server',
  authServerRedirectUrl:
    'https://fda-mystudies.domain.com/auth-server/callback',
  hydraClientId: 'MYSTUDIES_OAUTH_CLIENT',
  appVersion: 'v0.1',
  termsPageTitle: 'Terms title goes here',
  termsPageDescription: 'Terms description goes here',
  aboutPageTitle: 'About page title goes here',
  aboutPageDescription: 'About page description goes here',
  copyright: 'Copyright 2020 Google LLC.',
};
