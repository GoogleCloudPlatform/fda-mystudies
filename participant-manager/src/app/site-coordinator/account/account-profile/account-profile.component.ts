import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {AccountService} from '../shared/account.service';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {UpdateProfile} from '../shared/profile.model';
import {Validators} from '@angular/forms';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {StateService} from 'src/app/service/state.service';

@Component({
  selector: 'account-profile',
  templateUrl: './account-profile.component.html',
  styleUrls: ['./account-profile.component.scss'],
})
export class AccountProfileComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  profileForm: FormGroup;
  constructor(
    private readonly fb: FormBuilder,
    private readonly accountService: AccountService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly toastr: ToastrService,
    private readonly userState: StateService,
  ) {
    super();
    this.profileForm = fb.group({
      // eslint-disable-next-line @typescript-eslint/unbound-method
      email: ['', Validators.required],
      firstName: [
        '',
        // eslint-disable-next-line @typescript-eslint/unbound-method
        [Validators.required],
      ],
      lastName: [
        '',
        // eslint-disable-next-line @typescript-eslint/unbound-method
        [Validators.required],
      ],
    });
  }

  get f() {
    return this.profileForm.controls;
  }

  ngOnInit(): void {
    this.getProfileDetails();
  }

  getProfileDetails(): void {
    this.accountService.fetchUserProfile().subscribe(
      (data) => {
        this.profileForm.patchValue(data);
      },
      (error) => {
        this.toastr.error(error);
      },
    );
  }

  updateProfile(): void {
    if (!this.profileForm.valid) return;
    const profileToBeUpdated: UpdateProfile = {
      firstName: String(this.profileForm.controls['firstName'].value),
      lastName: String(this.profileForm.controls['lastName'].value),
    };
    this.accountService.updateUserProfile(profileToBeUpdated).subscribe(
      (successResponse: ApiResponse) => {
        this.userState.setCurrentUserName(
          this.profileForm.controls['firstName'].value,
        );
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
    void this.router.navigate(['coordinator/studies']);
  }

  signOut(): void {
    this.accountService.logout().subscribe(
      (successResponse: ApiResponse) => {
        if (getMessage(successResponse.code)) {
          this.toastr.error(getMessage(successResponse.code));
        }
        sessionStorage.clear();
        void this.router.navigate(['/']);
      },
      (errorResponse: ApiResponse) => {
        console.log(errorResponse);
        if (getMessage(errorResponse.code)) {
          this.toastr.error(getMessage(errorResponse.code));
        }
      },
    );
  }
}
