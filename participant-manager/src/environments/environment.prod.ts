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
  termsPageTitle: 'terms title goes here',
  termsPageDescription: 'terms description goes here',
  aboutPageTitle: 'about page title goes here',
  aboutPageDescription: 'about page description goes here',
};
