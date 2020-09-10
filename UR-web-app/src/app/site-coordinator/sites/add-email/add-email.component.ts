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
  @Output() cancelled = new EventEmitter();
  @Output() successSubmit = new EventEmitter();
  @Input() siteId = '';
  submitted = false;
  model = new AddEmail('');
  constructor(
    private readonly siteDetailedService: SiteDetailsService,
    private readonly toastr: ToastrService,
  ) {
    super();
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
              this.successSubmit.emit();
            }
          },
          () => {
            this.successSubmit.emit();
          },
        ),
    );
  }
  cancel(): void {
    this.cancelled.emit();
  }
}
