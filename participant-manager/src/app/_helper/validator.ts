/* eslint-disable */
import {FormGroup, ValidatorFn, AbstractControl} from '@angular/forms';
export function emailValiadtor(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    const emailFilter = /^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
    const validEmail = emailFilter.test(control.value);
    if (control.value === '') {
      return {emptyError: true};
    } else if (!validEmail) {
      return {invalidEmailid: true};
    }
    return null;
  };
}
export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: boolean} | null => {
    const uppercasePattern = /[A-Z]/g;
    const lowercasePattern = /[a-z]/g;
    const numericPattern = /[0-9]/g;
    const specialCharsPattern = /^[\w&.-]+$/;

    const value = control.value as string;
    if (control.value === '') {
      return {emptyError: true};
    } else if (
      value.length < 8 ||
      value.length > 64 ||
      !uppercasePattern.test(control.value) ||
      !lowercasePattern.test(control.value) ||
      specialCharsPattern.test(control.value) ||
      !numericPattern.test(control.value)
    ) {
      return {validPassword: true};
    }
    return null;
  };
}
export function mustMatch(controlName: string, matchingControlName: string) {
  return (formGroup: FormGroup) => {
    const control = formGroup.controls[controlName];
    const matchingControl = formGroup.controls[matchingControlName];
    if (matchingControl.errors && !matchingControl.errors.mustMatch) {
      return;
    }
    if (control.value !== matchingControl.value) {
      matchingControl.setErrors({mustMatch: true});
    } else {
      matchingControl.setErrors(null);
    }
  };
}
