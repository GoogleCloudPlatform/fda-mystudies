export enum SuccessCodesEnum {
  /* eslint-disable @typescript-eslint/naming-convention */
  MSG_001 = 'Location added successfully',
  MSG_0002 = 'Reactivate successfully',
  MSG_0003 = 'Deactivated successfully',
  MSG_0004 = 'Location updated successfully',
  /* eslint-enable @typescript-eslint/naming-convention */
}

type SuccessCodesStrings = keyof typeof SuccessCodesEnum;
export function getMessage(key: SuccessCodesStrings): string {
  return SuccessCodesEnum[key];
}
