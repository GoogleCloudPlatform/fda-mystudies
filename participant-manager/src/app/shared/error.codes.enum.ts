const errorMessages = {
  /* eslint-disable @typescript-eslint/naming-convention */
  EC_0001:
    'Due to consecutive failed sign-in attempts with incorrect password, your account has been locked for a period of 15 minutes. Please check your registered email inbox for assistance to reset your password in this period or wait until the lock period is over to sign in again.',
  EC_0002:
    'The temporary password entered is either invalid or expired. Please use the Forgot Password link to get password help.',
  EC_0003: 'Your account has been deactivated',
  EC_0004: 'Site(s) not found',
  EC_0005: 'Wrong email or password. Try again or click Forgot Password',
  EC_0006:
    'Your password has expired. Please use the Forgot Password link to get password help.',
  EC_0007:
    'This email has already been used. Please try with a different email address.',
  EC_0008:
    'Sorry, an error occurred and we could not send you the email . Please try again later.',
  EC_0009:
    'Sorry, an error has occurred and your request could not be processed. Please try again later.',
  EC_0010: 'The current password entered is incorrect',
  EC_0011:
    'Your new password should not match any of your previous 10 passwords',
  EC_0012: 'This admin does not have an active account',
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
  EC_0022:
    'Site already exists for this combination of location ID and study ID',
  EC_0023: 'You do not have permission to view or add or update locations',
  EC_0024: 'User not found',
  EC_0025: 'Location ID already exists',
  EC_0026:
    'This email either does not exist or has not been sent an invitation yet.',
  EC_0027: `This location already has 'Active' status`,
  EC_0028: 'This site cannot be modified',
  EC_0029: 'This location is already decommissioned',
  EC_0030: 'You do not have permission to update the location',
  EC_0031: 'Participant(s) cannot be added to the registry of an open study',
  EC_0032: 'The admin must have at least one permission assigned',
  EC_0033: 'Invalid security code',
  EC_0035: 'Error in getting participants details',
  EC_0036: 'The uploaded file is not in the prescribed format',
  EC_0037: 'The uploaded file does not adhere to the given template',
  EC_0038: 'Enrollment target update failed (the study is a closed study)',
  EC_0039:
    'Enrollment target failed to be updated (the site is decommissionned)',
  EC_0040: 'Error in getting consent data',
  EC_0041: `Allowed values for 'fields' are studies, sites`,
  EC_0042: 'Admin user not found',
  EC_0043:
    'Your account is pending activation. Please check your email for details and sign in to complete activation.',
  EC_0044:
    'Your account is pending activation. Please check your email for details.',
  EC_0045: 'An account with this email is already registered. Please sign in.',
  EC_0046: 'User does not exist',
  EC_0047: 'You do not have permission to view/edit this study',
  EC_0048: 'You do not have permission to manage this site',
  EC_0049: `Site doesn't exist or is inactive`,
  EC_0050: 'Allowed values are: N, D, I and E',
  EC_0051:
    'The decommission action cannot be taken with this site as it belongs to an open study',
  EC_0052: 'Invalid admin user status',
  EC_0053: 'Cannot add site to an open study',
  EC_0054: 'User ID is required',
  EC_0055:
    'There should be at least 4 unique characters that are different from your previous password',
  EC_0056: 'Your account has been temporary locked. Please try after sometime.',
  EC_0057: 'Please upload a .xls or .xlsx file',
  EC_0058: 'Location ID must be unique across the location directory',
  EC_0059: 'The password reset link is either expired or invalid',
  EC_0060: 'Enter a valid email',
  EC_0061:
    'The participant record cannot be enabled as it already exists in enabled state in another site of the same study.',
  EC_0062:
    'This view displays study-wise enrollment if you manage multiple sites.',
  EC_0063:
    'This view displays app-wise enrolment if you manage multiple studies.',
  EC_0064: 'The email already exists',
  EC_0122: 'Sites cannot be added using decommissioned locations.',
  EC_0123: 'You do not have permission to access this app.',
  EC_0124:
    'This study is deactivated. Sites cannot be added to deactivated studies.',
  EC_0125:
    '1 or more participant record(s) could not be enabled. This could happen if the emails exist in enabled state in another site of the same study.',
  EC_0065: `This admin's account is already active. Please try deactivating instead if you wish to revoke access to the Participant Manager.`,
  EC_0066:
    'The token entered is no longer valid. Please contact the site coordinator for assistance.',
  EC_0067: 'Account created successfully',
  EC_0068: 'Sorry, a location with this name already exists',
  EC_0069:
    'This site cannot be activated as the associated location is decommissioned',
  EC_0400: 'Invalid entries found in the submitted form. Please try again.',
  EC_0070: 'No sites found',
  EC_0071:
    'This view displays study-wise enrollment if you manage multiple sites.',
  EC_0072:
    'This view displays app-wise enrollment if you manage multiple studies.',
  EC_0121: `Invalid 'source' value`,
  EC_0127:
    'This study is deactivated. Sites cannot be re-activated for deactivated studies.',
  EC_0128: 'EmailId or password is blank in request',
  EC_0129: 'ApplicationId is missing in request header',
  EC_0130: 'Invalid data sharing status.',
  EC_0131: 'Temporary password is invalid',
  EC_0073:
    'Sorry, an error occurred and your feedback could not be sent to the organization. Please retry in some time',
  EC_0074:
    'Sorry, an error occurred and your inquiry could not be sent to the organization. Please retry in some time.',
  EC_0075:
    'Sorry, an error occurred and we could not send you the email required to complete account activation. Please try again.',
  EC_0076: 'Invalid sortby value',
  EC_0077: 'Invalid sorting direction',
  EC_0078: 'Temporary password is incorrect',
  EC_0079:
    'This site belongs to an active study that has one or more actively enrolled participants, and cannot be decommissioned.',
  EC_0081: 'Consecutive characters and space is not allowed',
  EC_0082: 'Password should not contain user name',
  EC_0083: 'Password should not contain service name',
  EC_0084: 'Please enter strong password',
  EC_0086:
    'This is a default location ID in use by the system already. Please enter a different location ID.',
  EC_0087:
    'This is a default location name in use by the system already. Please enter a different location name.',
  /* eslint-enable @typescript-eslint/naming-convention */
};
export type ErrorCode = keyof typeof errorMessages;

export function getMessage(key: ErrorCode): string {
  return errorMessages[key];
}
