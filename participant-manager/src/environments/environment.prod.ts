// Replace the example domain name with your deployed address.
export const environment = {
  production: true,
  // remove http/https to appear relative. xsrf-token skips absolute paths.
  baseUrl: '//fda-mystudies.domain.com/participant-manager-datastore',
  baseHref: '/participant-manager/',
  loginUrl: 'https://fda-mystudies.domain.com/oauth2/auth',
  authServerUrl: 'https://fda-mystudies.domain.com/auth-server',
  redirectUrl: 'https://fda-mystudies.domain.com/auth-server/callback',
  clientId: 'MYSTUDIES_OAUTH_CLIENT',
  appVersion: 'v0.1',
};
