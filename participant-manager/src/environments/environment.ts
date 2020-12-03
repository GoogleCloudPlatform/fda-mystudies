// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
export const environment = {
  production: false,
  participantManagerDatastoreUrl:
    'http://35.193.185.224:8093/participant-manager-service',

  hydraLoginUrl: 'https://35.193.185.224:9000/oauth2/auth',
  hydraClientId: 'MYSTUDIES_OAUTH_CLIENT',

  authServerUrl: 'http://35.193.185.224:8087/oauth-scim-service',
  authServerRedirectUrl: 'https://34.69.210.52/qa/oauth-scim-service/callback',

  appVersion: 'v0.1',

  // Branding centralisation
  aboutPageHtmlContent:
    '<div> <div class="mb__mb-sm"> <div class="p-none dashboard-card__title">Title style goes here</div> </div> <div class="participant__label"> Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. </div> </div> <div> <div class="mb__mb-sm mt-4"> <div class="p-none dashboard-card__title">Title style goes here</div> </div> <div class="participant__label"> Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. </div> </div>',
  termsPageHtmlContent:
    '<div> <div class="mb__mb-sm"> <div class="p-none dashboard-card__title">Title style goes here</div> </div> <div class="participant__label"> Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. </div> </div> <div> <div class="mb__mb-sm mt-4"> <div class="p-none dashboard-card__title">Title style goes here</div> </div> <div class="participant__label"> Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. </div> </div>',
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
