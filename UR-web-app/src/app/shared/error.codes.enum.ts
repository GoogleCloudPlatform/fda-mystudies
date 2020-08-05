const errorMessages = {
  ER_001: 'You do not have permission to view or add or update locations',
  ER_002: 'Failed to update Location',
  ER_003: `Can't reactive an already active location`,
  ER_004: `Can't decommision an already decommissioned location`,
};

export type ErrorCode = keyof typeof errorMessages;

export function getMessage(key: ErrorCode): string {
  return errorMessages[key];
}
