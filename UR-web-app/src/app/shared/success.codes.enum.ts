const successMessages = {
  /* eslint-disable @typescript-eslint/naming-convention */
  MSG_001: 'Location added successfully',
  MSG_0002: 'Reactivate successfully',
  MSG_0003: 'Deactivated successfully',
  MSG_0004: 'Location updated successfully',
  MSG_0016: 'User profile with security code fetched successfully',
  /* eslint-enable @typescript-eslint/naming-convention */
};

export type SuccessCode = keyof typeof successMessages;

export function getMessage(key: SuccessCode): string {
  return successMessages[key];
}
