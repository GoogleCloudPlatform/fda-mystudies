/* eslint-disable */
import {FormGroup, ValidatorFn, AbstractControl} from '@angular/forms';
import {isNgTemplate} from '@angular/compiler';
import {commonusePassword} from '../jsondata/datajson';
import {keyframes} from '@angular/animations';
import {getMessage} from '../shared/error.codes.enum';

let AlphaBeticcombin = [
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
];

let AlphaBeticcombintemp = [
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
let numericalcombin = [
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
    let specialCharacterUsed = false;
    let numberUsed = false;
    let user = JSON.parse(sessionStorage.user);
    let name = user.firstName.toLowerCase();
    let last = user.lastName.toLowerCase();
    let flName = name + last;
    let lfName = last + name;
    // let specialCharsPattern = /^[a-zA-Z0-9!@#$%^&*()‘+,:;<>{}~|-]+$/;
    let specialCharsPattern = control.value.match(
      /([!@#$%^&*()‘+,:;<>{}~|-])\1*/g,
    );

    let numberPattern = control.value.match(/([0-9])\1*/g);

    if (specialCharsPattern != null) {
      specialCharacterUsed = true;
    }

    if (numberPattern != null) {
      numberUsed = true;
    }

    var patternCapitaletter = /[A-Z]/g;
    var patternSmallletter = /[a-z]/g;
    var patternSmallCapletter = /[a-zA-Z]/g;

    let firstNameUsed = false;
    let lastNameUsed = false;
    let firstNameLastNameUsed = false;
    let lastNameFirstNameUsed = false;
    firstNameUsed = control.value
      .toLowerCase()
      .includes(user.firstName.toLowerCase());
    lastNameUsed = control.value
      .toLowerCase()
      .includes(user.lastName.toLowerCase());
    firstNameLastNameUsed = control.value
      .toLowerCase()
      .includes(flName.toLowerCase());
    lastNameFirstNameUsed = control.value
      .toLowerCase()
      .includes(lfName.toLowerCase());

    let consecutiveIdenticalCharacter: boolean = false;
    let consecutivewhitespaceStatus: boolean = false;
    let easyGuessingNumbersOralphanumeric = false;

    if (control.value.length >= 3) {
      for (let i = 0; i < AlphaBeticcombintemp.length; i++) {
        let m = AlphaBeticcombintemp[i].toLowerCase();
        var patt = new RegExp(m);
        var ressd = patt.test(control.value);
        if (ressd) {
          easyGuessingNumbersOralphanumeric = true;
          console.log(ressd);
          break;
        }
        // let  ressd = control.value.match(/(AlphaBeticcombin[i])/g);
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
      return {consecutiveCharacterorwhitespace: true};
    } else if (control.value.length < 8) {
      return {passwordlength: true};
    } else if (serviceNameStatus) {
      return {serviceNameError: true};
    } else if (
      firstNameUsed ||
      lastNameUsed ||
      firstNameLastNameUsed ||
      lastNameFirstNameUsed
    ) {
      return {firstNameUseError: true};
    } else if (
      commonusepasswordStatus ||
      !specialCharacterUsed ||
      !numberUsed ||
      !patternSmallCapletter.test(control.value) ||
      !patternSmallletter.test(control.value) ||
      !patternCapitaletter.test(control.value) ||
      control.value.length < 8 ||
      control.value.length > 64
    ) {
      return {hi: true};
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

    if (control.value.length > 0) {
      console.log(control.value);
    }
    let name = firstName.value.toLowerCase();
    let last = lastName.value.toLowerCase();
    let flName = name + last;
    let lfName = last + name;
    let firstNameUsed = false;
    let lastNameUsed = false;
    let firstNameLastNameUsed = false;
    let lastNameFirstNameUsed = false;
    firstNameUsed = control.value
      .toLowerCase()
      .includes(firstName.value.toLowerCase());
    lastNameUsed = control.value
      .toLowerCase()
      .includes(lastName.value.toLowerCase());
    firstNameLastNameUsed = control.value
      .toLowerCase()
      .includes(flName.toLowerCase());
    lastNameFirstNameUsed = control.value
      .toLowerCase()
      .includes(lfName.toLowerCase());

    if (control.value == '') {
      control.setErrors({Valuerequired: true});
    } else if (
      firstNameUsed ||
      lastNameUsed ||
      firstNameLastNameUsed ||
      lastNameFirstNameUsed
    ) {
      control.setErrors({firstNameUseError: true});
    } else {
      return null;
    }
  };
}
