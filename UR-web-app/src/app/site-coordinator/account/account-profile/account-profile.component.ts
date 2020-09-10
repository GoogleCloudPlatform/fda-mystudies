/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {AccountService} from '../shared/account.service';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {UpdateProfile} from '../shared/profile.model';
import {Observable, of} from 'rxjs';
import {Validators} from '@angular/forms';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {User} from 'src/app/entity/user';

@Component({
  selector: 'account-profile',
  templateUrl: './account-profile.component.html',
  styleUrls: ['./account-profile.component.scss'],
})
export class AccountProfileComponent extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  pofile$: Observable<User> = of();
  constructor(
    private readonly fb: FormBuilder,
    private readonly accountService: AccountService,
    private readonly route: ActivatedRoute,
    private readonly toastr: ToastrService,
  ) {
    super();
  }

  errorMessage = '';
  successMessage = '';
  passCritiria = '';
  // eslint-disable-next-line no-invalid-this
  profileForm = this.fb.group({
    // eslint-disable-next-line @typescript-eslint/unbound-method
    email: ['', Validators.required],
    firstName: [
      '',
      // eslint-disable-next-line @typescript-eslint/unbound-method
      [Validators.required, Validators.pattern('^[A-Za-z]{3,50}$')],
    ],
    lastName: [
      '',
      // eslint-disable-next-line @typescript-eslint/unbound-method
      [Validators.required, Validators.pattern('^[A-Za-z]{3,50}$')],
    ],
  });

  get f() {
    return this.profileForm.controls;
  }

  ngOnInit(): void {
    this.getProfileDetails();
  }

  getProfileDetails(): void {
    this.accountService.fetchProfile().subscribe(
      (data) => {
        this.profileForm.patchValue(data);
      },
      (error) => {
        this.toastr.error(error.error.userMessage);
      },
    );
  }

  updateProfile(): void {
    if (!this.profileForm.valid) return;

    const profileToBeUpdated: UpdateProfile = {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      firstName: this.profileForm.value.firstName,
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      lastName: this.profileForm.value.lastName,
    };
    this.accountService.updateUserProfile(profileToBeUpdated).subscribe(
      (successResponse: ApiResponse) => {
        this.toastr.success(successResponse.message);
      },
      (errorResponse: ApiResponse) => {
        if (getMessage(errorResponse.code)) {
          this.toastr.success(getMessage(errorResponse.code));
        }
      },
    );
  }

  cancel(): void {
    this.getProfileDetails();
  }
}
