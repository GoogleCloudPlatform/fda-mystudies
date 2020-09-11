import {Component, EventEmitter, Output, Input} from '@angular/core';
import {AddEmail} from '../shared/add-email';
import {SiteDetailsService} from '../shared/site-details.service';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {ToastrService} from 'ngx-toastr';
@Component({
  selector: 'app-add-email',
  templateUrl: './add-email.component.html',
  styleUrls: ['./add-email.component.scss'],
})
export class AddEmailComponent extends UnsubscribeOnDestroyAdapter {
  @Output() cancel = new EventEmitter();
  // eslint-disable-next-line @angular-eslint/no-output-native
  @Output() submit = new EventEmitter();
  @Input() siteId = '';
  submitted = false;
  model: AddEmail;
  constructor(
    private readonly siteDetailedService: SiteDetailsService,
    private readonly toastr: ToastrService,
  ) {
    super();
    this.model = {email: ''};
  }

  addParticipant(): void {
    this.submitted = true;
    this.subs.add(
      this.siteDetailedService
        .addParticipants(this.siteId, this.model)
        .subscribe(
          (successResponse: ApiResponse) => {
            if (getMessage(successResponse.code)) {
              this.toastr.success(getMessage(successResponse.code));
            } else {
              this.toastr.success(successResponse.message);
              this.submit.emit();
            }
          },
          (errorResponse: ApiResponse) => {
            if (getMessage(errorResponse.code)) {
              this.toastr.error(getMessage(errorResponse.code));
            }
          },
        ),
    );
  }
  onCancel(): void {
    this.cancel.emit();
  }
}
