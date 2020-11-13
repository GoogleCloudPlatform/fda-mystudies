const errorMessages = {
  /* eslint-disable @typescript-eslint/naming-convention */
  EC_0001:
    'Due to consecutive failed sign-in attempts with incorrect password, your account has been locked for a period of 15 minutes. Please check your registered email inbox for assistance to reset your password in this period or wait until the lock period is over to sign in again.',
  EC_0002:
    'Your temporary password is expired. Please use the forgot password link to reset your password.',
  EC_0003: 'Your account has been deactivated',
  EC_0004: 'Site not found',
  EC_0005: 'Wrong email or password. Try again or click forgot password.',
  EC_0006:
    'Your password is expired. Please use the forgot password link to reset your password.',
  EC_0007:
    'This email has already been used. Please try with a different email address.',
  EC_0008:
    'Your verification email was unable to send because the connection to mail server was interrupted',
  EC_0009:
    'Sorry, an error has occurred and your request could not be processed. Please try again later.',
  EC_0010: 'Current password entered is invalid',
  EC_0011: 'Your new password cannot repeat any of your previous 10 passwords',
  EC_0012: 'User is not active',
  EC_0013: 'App not found',
  EC_0014: 'Study not found',
  EC_0015: 'Location not found',
  EC_0016:
    'This location is being used as an active site in one or more studies and cannot be decomissioned',
  EC_0017: 'You are not authorized to access this information',
  EC_0018: 'The request cannot be fulfilled due to bad syntax',
  EC_0019: 'Unauthorized or Invalid token',
  EC_0020: 'Email ID or status to be provided',
  EC_0021: 'You do not have permission to access this site',
  EC_0022: 'Site exists with the given location ID and study ID',
  EC_0023: 'You do not have permission to view or add or update locations',
  EC_0024: 'User not found',
  EC_0025: 'Location ID already exists',
  EC_0026: 'Provided email ID does not exists or user is not invited',
  EC_0027: `Can't activate an already actived location`,
  EC_0028: `Default site can't be modified`,
  EC_0029: `Can't decommision an already decommissioned location`,
  EC_0030: 'You do not have permission to update the location',
  EC_0031: 'Participant cannot be added to open study',
  EC_0032: 'User should have atleast one permission',
  EC_0033: 'Invalid security code',
  EC_0035: 'Error in getting participants details',
  EC_0036: 'Import document not in prescribed format',
  EC_0037:
    'Unable to import the document due to invalid format in the document content',
  EC_0038: 'Enrollment target update failed for closed study',
  EC_0039: 'Enrollment target failed to be updated decommissionned site',
  EC_0040: 'Error in getting consent data',
  EC_0041: `Allowed values for 'fields' are studies, sites`,
  EC_0042: 'Admin user not found',
  EC_0043:
    'Your account verification is pending.Please check your email for the activation link.',
  EC_0044:
    '"Your account is not verified. Please verify your account by clicking on the link which has been sent to your registered email. if not received, would you like to resend verification link?',
  EC_0045: 'User with same email has already been registered. Please log in.',
  EC_0046: 'User does not exist',
  EC_0047: 'You do not have permission to view/add study',
  EC_0048: 'You do not have permission to manage site',
  EC_0049: `Site doesn't exists or is inactive`,
  EC_0050: `Allowed values are: N, D, I and E`,
  EC_0051: 'Cannot decomission site as study type is open',
  EC_0052: 'Invalid user status',
  EC_0053: 'Cannot add site to open study',
  EC_0054: 'User Id is required',
  EC_0055:
    'There should be at least 4 unique characters that are different from your previous password',
  EC_0056: 'Your account has been temporary locked.Please try after sometime.',
  EC_0057: 'Please upload a .xls or .xlsx file',
  EC_0058: 'Location ID must be unique across the location directory',
  EC_0059: 'The password reset link is either expired or invalid',
  EC_0060: 'Entered email is invalid',
  EC_0061:
    'Invitation cannot be enabled as participant record is enabled in another site with in same study.',
  EC_0062:
    'You need to have permissions to one or more sites to see study level information',
  EC_0063:
    'You need to have permission to one or more studies to see app level information.',
  EC_0064: 'email_id already exist',
  EC_0122: 'Cannot add site for decommissioned location.',
  EC_0123: 'You do not have permission to access this app',
  EC_0124: 'Cannot add site to Deactivated study',
  EC_0125:
    'Invitation cannot be enabled as participant record is enabled in another site with in same study.',
  EC_0069:
    'This site cannot be activated as the associated location is decommissioned',
  /* eslint-enable @typescript-eslint/naming-convention */
};
export type ErrorCode = keyof typeof errorMessages;

export function getMessage(key: ErrorCode): string {
  return errorMessages[key];
}
