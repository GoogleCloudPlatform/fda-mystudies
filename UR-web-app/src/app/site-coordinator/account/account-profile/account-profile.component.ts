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

@Component({
  selector: 'account-profile',
  templateUrl: './account-profile.component.html',
  styleUrls: ['./account-profile.component.scss'],
})
export class AccountProfileComponent extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  profileForm: FormGroup;
  constructor(
    private readonly fb: FormBuilder,
    private readonly accountService: AccountService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly toastr: ToastrService,
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
    this.accountService.fetchProfile().subscribe(
      (data) => {
        this.profileForm.patchValue(data);
      },
      (error) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        this.toastr.error(error.error.userMessage);
      },
    );
  }

  updateProfile(): void {
    if (!this.profileForm.valid) return;
    const profileToBeUpdated: UpdateProfile = {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      firstName: String(this.profileForm.value.firstName),
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      lastName: String(this.profileForm.value.lastName),
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

  signOut(): void {
    this.accountService.logout().subscribe(
      (successResponse: ApiResponse) => {
        this.toastr.success(successResponse.message);
        void this.router.navigate(['/']);
      },
      (errorResponse: ApiResponse) => {
        if (getMessage(errorResponse.code)) {
          this.toastr.success(getMessage(errorResponse.code));
        }
      },
    );
  }
}
