export enum ErrorCodesEnum {
  /* eslint-disable @typescript-eslint/naming-convention */
  ER_001 = 'You do not have permission to view or add or update locations',
  ER_002 = 'Failed to update Location',
  ER_003 = `Can't reactive an already active location`,
  ER_004 = `Can't decommision an already decommissioned location`,
  /* eslint-enable @typescript-eslint/naming-convention */
}
type ErrorCodesStrings = keyof typeof ErrorCodesEnum;
export function getMessage(key: ErrorCodesStrings): string {
  return ErrorCodesEnum[key];
}
