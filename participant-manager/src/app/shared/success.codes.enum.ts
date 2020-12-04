const successMessages = {
  /* eslint-disable @typescript-eslint/naming-convention */
  MSG_0001: 'Site added successfully',
MSG_0002: 'New location added successfully',
MSG_0003: 'Apps fetched successfully',
MSG_0004: 'Study details fetched successfully',
MSG_0005: 'Site decommissioned successfully',
MSG_0006: 'Site activated successfully',
MSG_0007: 'Location details updated successfully',
MSG_0008: 'Participant registry fetched successfully',
MSG_0009: 'Email added successfully',
MSG_0010: 'App details fetched successfully',
MSG_0011: 'Target enrollment updated successfully',
MSG_0012: 'App participants fetched successfully',
MSG_0013: 'New user created and invite sent successfully',
MSG_0014: 'User details updated successfully',
MSG_0015: 'User profile fetched successfully',
MSG_0016: 'User profile with security code fetched successfully',
MSG_0017: 'Participant details fetched successfully',
MSG_0018: 'Invitation sent successfully',
MSG_0019: 'Your password has been reset successfully',
MSG_0020: 'Email accepted by receiving mail server',
MSG_0021: 'Email list imported successfully',
MSG_0022: 'Status updated successfully',
MSG_0023: 'Sites fetched successfully',
MSG_0024: 'Consent document fetched successfully',
MSG_0025: 'User details updated successfully',
MSG_0026: 'Account created successfully',
MSG_0027: 'Admin details fetched successfully',
MSG_0028: 'User deactivated successfully',
MSG_0029: 'User activated successfully',
MSG_0030: 'Location decommisioned successfully',
MSG_0031: 'Location activated successfully',
MSG_0032: 'Location fetched successfully',
MSG_0033: 'Locations for site fetched successfully',
MSG_0034: 'Profile updated successfully',
MSG_0035: 'Your password has been changed successfully',
MSG_0036: 'User details fetched successfully',
MSG_0037: `The email list was imported with the following issues:

<Number> emails failed to import.

Reason for import failure for these could be one of the following:
1.Email not in proper format 
2.Duplicate emails exist in the list 
3.Participant enabled in another site within the same study
4.Email already exists in the site
5. The email already exists in enabled state for another site in the same study.
`,
MSG_0038: 'User details fetched successfully',
MSG_0039: 'Invitation disabled successfully',
MSG_0040: 'Invitation enabled successfully',
MSG_0041: 'Password saved successfully',
MSG_0042: 'Email added successfully',
MSG_0043: 'Invitation to user resent successfully',
MSG_0044: 'Password help has been sent to your registered email',
MSG_0045: 'Invitation to user deleted successfully',
  /* eslint-enable @typescript-eslint/naming-convention */
};

export type SuccessCode = keyof typeof successMessages;

export function getMessage(key: SuccessCode): string {
  return successMessages[key];
}

export function getSuccessMessage(key: SuccessCode, message: string): string {
  // eslint-disable-next-line no-prototype-builtins
  if (successMessages.hasOwnProperty(key)) {
    return successMessages[key];
  } else if (message === '') {
    console.error(
      'Success message code is undefined in response, return default success message.',
    );
    return 'success';
  }
  return message;
}
