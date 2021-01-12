// Replace the example domain name with your deployed address.
export const environment = {
  production: true,
  // remove http/https to appear relative. xsrf-token skips absolute paths.
  participantManagerDatastoreUrl:
    '//<BASE_URL>/participant-manager-datastore',
  baseHref: '/participant-manager/',
  hydraLoginUrl: 'https://<BASE_URL>/oauth2/auth',
  authServerUrl: 'https://<BASE_URL>/auth-server',
  authServerRedirectUrl:
    'https://<BASE_URL>/auth-server/callback',
  hydraClientId: '<AUTH_SERVER_CLIENT_ID>',
  appVersion: 'v0.1',
  termsPageTitle: 'terms title goes here',
  termsPageDescription: 'terms description goes here',
  aboutPageTitle: 'about page title goes here',
  aboutPageDescription: 'about page description goes here',
};
