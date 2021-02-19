/* eslint-disable */
import {FormGroup, ValidatorFn, AbstractControl} from '@angular/forms';
import {commonusePassword} from '../jsondata/datajson';

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
    // const uppercasePattern = /[A-Z]/g;
    // const lowercasePattern = /[a-z]/g;
    // const numericPattern = /[0-9]/g;
    // const specialCharsPattern = /(?=.*[!"#$%&'()*+,:;<=>?@\[\]^_`\-{|}~])/;
    // const value = control.value as string;
    // if (control.value === '') {
    //   return {emptyError: true};
    // } else if (
    //   value.length < 8 ||
    //   value.length > 64 ||
    //   !uppercasePattern.test(control.value) ||
    //   !lowercasePattern.test(control.value) ||
    //   !specialCharsPattern.test(control.value) ||
    //   !numericPattern.test(control.value)
    // ) {
    //   return {validPassword: true};
    // }

    // return null;
    let commonlyusepassword = commonusePassword;
    let commonusepasswordStatus = false;
    let consecutiveSpecialCharExist = false;
    let predictionarywordBlockpassword = false;
    let domainName = window.location.hostname;
    let forbidenNameUsingDomain = new RegExp(domainName);

    let forbidenNameUsingDomainstatus = forbidenNameUsingDomain.test(
      control.value,
    );
    let consecutiveIdenticalCharacter: boolean = false;
    let consecutivewhitespaceStatus: boolean = false;
    var possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    // var possibleNumberCombination = "0123456789";
    var possibleNumberCombination = '987654321';
    let easyGuessingNumbersOralphanumeric = false;
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
    let easyGusingPassword = [
      'monkey',
      'p@ssw0rd',
      'expandedaccess',
      'reagan',
      'udal',
      'FDA',
      'Expanded Access eRequest',
      'www.expandedaccess.com',
      'expanded',
      '<script>',
      '</script>',
      'script',
      'alert',
    ];

    let pwdcombination: string[] = [];

    for (var x of AlphaBeticcombin) {
      for (var z of numericalcombin) {
        pwdcombination.push(x + z);
      }
    }

    if (control.value.length >= 8) {
      for (let k = 0; k < commonlyusepassword.length; k++) {
        if (control.value === commonlyusepassword[k]) {
          commonusepasswordStatus = true;
        }
      }
    }

    if (control.value.length >= 3) {
      for (let i = 0; i < easyGusingPassword.length; i++) {
        let m = easyGusingPassword[i].toLowerCase();
        var patt = new RegExp(m);
        var ressd = patt.test(control.value.toLowerCase());
        if (ressd) {
          predictionarywordBlockpassword = true;
          break;
        }
        // let  ressd = control.value.match(/(AlphaBeticcombin[i])/g);
      }
    }
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

    let forbiddenNumbers = ['0123456789'];
    let res = control.value.match(/([a-zA-Z0-9])\1*/g);
    // var consecutivespecilacharacterpatern = /(\W|_)\1*/g;
    let conseutiveSpecialChar = control.value.match(/(\W|_)\1*/g);
    if (conseutiveSpecialChar != null) {
      conseutiveSpecialChar.forEach(function (value: string) {
        if (value.length > 1) {
          consecutiveSpecialCharExist = true;
        }
      });
    }

    // var patternSpecialChar = /^[\w&.-]+$/;
    // let patternSpecialCharExist = control.value.match(/(^[\w&.-]+$)\1*/g);

    //for consecutive white space
    consecutivewhitespaceStatus = /\s{2,}/.test(control.value);

    if (res != null) {
      res.forEach(function (value: string) {
        if (value.length > 1) {
          consecutiveIdenticalCharacter = true;
        }
      });
    }

    var Validpassword: any;
    if (control.value == '') {
      // control.setErrors({Valuerequired: true});
      return {emptyError: true};
    } else if (
      consecutiveIdenticalCharacter ||
      consecutivewhitespaceStatus ||
      consecutiveSpecialCharExist
    ) {
      return {consecutiveCharacterorwhitespace: true};
    } else if (predictionarywordBlockpassword) {
      return {preDictionarypasswordError: true};
    } else if (easyGuessingNumbersOralphanumeric) {
      return {easyguessingPassworderror: true};
    } else if (commonusepasswordStatus) {
      return {commonusePasswordError: true};
    } else if (control.value.length < 8 || control.value.length > 64) {
      return {passwordlength: true};
    } else {
      control.setErrors(null);
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
