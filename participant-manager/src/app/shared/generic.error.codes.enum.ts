const genericErrorMessages = {
  /* eslint-disable @typescript-eslint/naming-convention */
  EC_0034:
    'This link is no longer valid to be used. For any assistance needed with your account, please contact the system admin.',
  EC_0080:
    'Your session has been terminated as a security measure, either due to inactivity or an account update. Please sign in again to continue.',
  EC_0081: 'Consecutive characters and space is not allowed',
  EC_0082: 'Password should not contain user name',
  EC_0083: 'Password should not contain service name',
  EC_0084: 'Please enter strong password',

  /* eslint-enable @typescript-eslint/naming-convention */
};
export type GenericErrorCode = keyof typeof genericErrorMessages;

export function getGenericMessage(key: GenericErrorCode): string {
  return genericErrorMessages[key];
}
