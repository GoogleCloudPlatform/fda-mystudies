/* eslint-disable */
import {FormGroup, ValidatorFn, AbstractControl} from '@angular/forms';
import {commonusePassword} from '../jsondata/datajson';

let AlphabeticCombinTemp = [
  'ABC',
  'BCD',
  'CDE',
  'DEF',
  'EFG',
  'FGH',
  'GHI',
  'HIJ',
  'IJK',
  'JKL',
  'KLM',
  'LMN',
  'MNO',
  'NOP',
  'OPQ',
  'PQR',
  'QRS',
  'RST',
  'STU',
  'TUV',
  'UVW',
  'VWX',
  'WXY',
  'XYZ',
  '012',
  '123',
  '234',
  '345',
  '456',
  '567',
  '678',
  '789',
  '987',
  '876',
  '765',
  '654',
  '543',
  '432',
  '321',
  '210',
];
let serviceName = [
  'Participant',
  'Manager',
  'ParticipantManager',
  'ManagerParticipant',
  'Participant-Manager',
];

export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: boolean} | null => {
    let commonlyusepassword = commonusePassword;
    let commonusepasswordStatus = false;
    let serviceNameStatus = false;
    let consecutiveSpecialCharExist = false;
    let user = JSON.parse(sessionStorage.user);
    var patternForAlphabets = /(?=.*[a-z])(?=.*[A-Z])/g;
    let consecutiveIdenticalCharacter: boolean = false;
    let consecutivewhitespaceStatus: boolean = false;
    let easyGuessingNumbersOralphanumeric = false;

    if (control.value.length >= 3) {
      for (let i = 0; i < AlphabeticCombinTemp.length; i++) {
        let m = AlphabeticCombinTemp[i].toLowerCase();
        var patt = new RegExp(m);
        var ressd = patt.test(control.value);
        if (ressd) {
          easyGuessingNumbersOralphanumeric = true;
          console.log(ressd);
          break;
        }
      }
    }

    let res = control.value.match(/([a-zA-Z0-9])\1*/g);
    let consecutivewhitespacePattern = control.value.match(/[\s-]\1*/g);
    if (consecutivewhitespacePattern != null) {
      consecutivewhitespaceStatus = true;
    }
    if (res != null) {
      res.forEach(function (value: any) {
        if (value.length > 1) {
          consecutiveIdenticalCharacter = true;
        }
      });
    }

    for (let k = 0; k < commonlyusepassword.length; k++) {
      if (
        control.value
          .toLowerCase()
          .includes(commonlyusepassword[k].toLowerCase())
      ) {
        commonusepasswordStatus = true;
      }
    }

    for (let k = 0; k < serviceName.length; k++) {
      if (control.value.toLowerCase().includes(serviceName[k].toLowerCase())) {
        serviceNameStatus = true;
      }
    }

    if (control.value == '') {
      return {Valuerequired: true};
    } else if (
      consecutiveIdenticalCharacter ||
      consecutivewhitespaceStatus ||
      consecutiveSpecialCharExist ||
      easyGuessingNumbersOralphanumeric
    ) {
      return {consecutiveCharactErrorwhitespace: true};
    } else if (control.value.length < 8) {
      return {passwordlength: true};
    } else if (serviceNameStatus) {
      return {serviceNameError: true};
    } else if (
      control.value.toLowerCase().includes(user.firstName.toLowerCase()) ||
      control.value.toLowerCase().includes(user.lastName.toLowerCase()) ||
      control.value
        .toLowerCase()
        .includes(user.firstName.toLowerCase() + user.lastName.toLowerCase()) ||
      control.value
        .toLowerCase()
        .includes(user.lastName.toLowerCase() + user.firstName.toLowerCase())
    ) {
      return {userNameError: true};
    } else if (
      commonusepasswordStatus ||
      control.value.match(/([!@#$%^&*()‘+,:;<>{}~|-])\1*/g) === null ||
      control.value.match(/([0-9])\1*/g) === null ||
      !patternForAlphabets.test(control.value) ||
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
