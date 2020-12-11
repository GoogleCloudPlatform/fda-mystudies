const successMessages = {
  /* eslint-disable @typescript-eslint/naming-convention */
  MSG_0001: 'Site added to study',
  MSG_0002: 'New location added',
  MSG_0003: 'Apps fetched successfully',
  MSG_0004: 'Study details fetched successfully',
  MSG_0005: 'Site decommissioned successfully',
  MSG_0006: 'The site has been activated.',
  MSG_0007: 'Location details updated',
  MSG_0008: 'Participant registry details fetched successfully',
  MSG_0009: 'Email added to site registry',
  MSG_0010: 'App details fetched successfully',
  MSG_0011: 'Enrollment target updated for the study',
  MSG_0012: 'App participants fetched successfully',
  MSG_0013:
    'The newly added user has been invited to use the Participant Manager',
  MSG_0014: 'User record updated',
  MSG_0015: 'User profile fetched successfully',
  MSG_0016: 'User profile with security code fetched successfully',
  MSG_0017: 'Participant details fetched successfully',
  MSG_0018: 'Study invitation sent to participant(s)',
  MSG_0019: 'Your password has been reset',
  MSG_0020: 'Email accepted by receiving mail server',
  MSG_0021: 'Email list imported successfully',
  MSG_0022: 'Onboarding status updated successfully',
  MSG_0023: 'Sites fetched successfully',
  MSG_0024: 'Consent document fetched successfully',
  MSG_0025: 'User record updated',
  MSG_0026: 'Your account is now set up',
  MSG_0027: 'Admin details fetched successfully',
  MSG_0028: 'User deactivated successfully',
  MSG_0029: 'User record activated',
  MSG_0030: 'Location decommisioned',
  MSG_0031: 'Location activated',
  MSG_0032: 'Location fetched successfully',
  MSG_0033: 'Locations for site fetched successfully',
  MSG_0034: 'Your account and profile details have been updated',
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
  MSG_0039: 'Invitation disabled for selected participant(s)',
  MSG_0040: 'Invitation enabled for selected participant(s)',
  MSG_0041: 'Password updated successfully',
  MSG_0042: 'Email added to site registry',
  MSG_0043: 'Account setup invitation resent',
  MSG_0044: 'Password help has been sent to your registered email',
  MSG_0045: 'The invitation for this user has been deleted',
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
