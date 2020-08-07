const successMessages = {
  MSG_001: 'Location added successfully',
  MSG_0002: 'Reactivate successfully',
  MSG_0003: 'Deactivated successfully',
  MSG_0004: 'Location updated successfully',
};

export type SuccessCode = keyof typeof successMessages;

export function getMessage(key: SuccessCode): string {
  return successMessages[key];
}
