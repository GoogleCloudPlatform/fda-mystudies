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
  aboutPageHtmlContent:
    '<div> <div class="mb__mb-sm"> <div class="p-none dashboard-card__title">Title style goes here</div> </div> <div class="participant__label"> Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. </div> </div> <div> <div class="mb__mb-sm mt-4"> <div class="p-none dashboard-card__title">Title style goes here</div> </div> <div class="participant__label"> Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. </div> </div>',
  termsPageHtmlContent:
    '<div> <div class="mb__mb-sm"> <div class="p-none dashboard-card__title">Title style goes here</div> </div> <div class="participant__label"> Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. </div> </div> <div> <div class="mb__mb-sm mt-4"> <div class="p-none dashboard-card__title">Title style goes here</div> </div> <div class="participant__label"> Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. </div> </div>',
};
