/* eslint-disable */
import {FormGroup, ValidatorFn, AbstractControl} from '@angular/forms';
export function emailvaliadtor(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    var email_filter = /^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
    const validemail = email_filter.test(control.value);

    return control.value == ''
      ? {emptyError: true}
      : validemail
      ? null
      : {invalidEmailid: true};
  };
}
export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: boolean} | null => {
    let user = JSON.parse(sessionStorage.user || 'null');
    if (control.value == '') {
      return {Valuerequired: true};
    } else if (
      control.value.match(/([a-zA-Z0-9])\1\1+/) !== null ||
      control.value.match(/\\b([a-zA-Z0-9])\\1\\1+\\b/) !== null ||
      control.value.match(/[\s-]\1*/g) !== null ||
      control.value.match(
        /(abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz|012|123|234|345|456|567|678|789)+/gi,
      ) !== null
    ) {
      return {consecutiveCharactErrorwhitespace: true};
    } else if (
      user !== null &&
      (control.value.toLowerCase().includes(user.firstName.toLowerCase()) ||
        control.value.toLowerCase().includes(user.lastName.toLowerCase()) ||
        control.value
          .toLowerCase()
          .includes(
            user.firstName.toLowerCase() + user.lastName.toLowerCase(),
          ) ||
        control.value
          .toLowerCase()
          .includes(user.lastName.toLowerCase() + user.firstName.toLowerCase()))
    ) {
      return {userNameError: true};
    } else if (
      control.value.match(
        /(Participant|Manager|ParticipantManager|ManagerParticipant|Participant-Manager)+/gi,
      ) !== null
    ) {
      return {serviceNameError: true};
    } else if (control.value.length < 8) {
      return {passwordlength: true};
    } else if (
      control.value.match(
        /(Pasword|P@sword|Qwerty|Eagles|Bears|Giants|Cowboy|Vikings|Chelsea|Arsenal|Manchesterunited|Participantmanager|Rams|Lions|Panthers|Jaguars|Texans|GoPatriots|Winter|Minecraft|Master|Shadow|Monkey|US city name|Country name|US State name)+/gi,
      ) !== null ||
      control.value.match(/([!@#$%^&*()‘+,:;<>{}~|-])\1*/g) === null ||
      control.value.match(/([0-9])\1*/g) === null ||
      control.value.match(/(?=.*[a-z])(?=.*[A-Z])/g) === null ||
      control.value.length > 64
    ) {
      return {error: true};
    } else {
      return null;
    }
  };
}

export function mustMatch(controlName: string, matchingControlName: string) {
  return (formGroup: FormGroup) => {
    const control = formGroup.controls[controlName];
    const matchingControl = formGroup.controls[matchingControlName];
    if (matchingControl.errors && !matchingControl.errors.mustMatch) {
      return;
    }
    if (control.value !== '' && control.value !== matchingControl.value) {
      matchingControl.setErrors({mustMatch: true});
    } else {
      matchingControl.setErrors(null);
    }
  };
}

export function newPasswordValidator(
  firstname: string,
  lastname: string,
  password: string,
) {
  return (formGroup: FormGroup) => {
    const control = formGroup.controls[password];
    const firstName = formGroup.controls[firstname];
    const lastName = formGroup.controls[lastname];

    if (control.value == '') {
      control.setErrors({Valuerequired: true});
    } else if (
      control.value.toLowerCase().includes(firstName.value.toLowerCase()) ||
      control.value.toLowerCase().includes(lastName.value.toLowerCase()) ||
      control.value
        .toLowerCase()
        .includes(
          firstName.value.toLowerCase() + lastName.value.toLowerCase(),
        ) ||
      control.value
        .toLowerCase()
        .includes(lastName.value.toLowerCase() + firstName.value.toLowerCase())
    ) {
      control.setErrors({userNameError: true});
    } else {
      return null;
    }
  };
}
