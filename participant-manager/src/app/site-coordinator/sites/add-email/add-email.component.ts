import {Component, EventEmitter, Output, Input} from '@angular/core';
import {AddEmail, AddEmailResponse} from '../shared/add-email';
import {SiteDetailsService} from '../shared/site-details.service';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {ToastrService} from 'ngx-toastr';
import {Participant} from '../shared/import-particpants';
@Component({
  selector: 'app-add-email',
  templateUrl: './add-email.component.html',
  styleUrls: ['./add-email.component.scss'],
})
export class AddEmailComponent extends UnsubscribeOnDestroyAdapter {
  @Output() cancel = new EventEmitter();
  @Output() addEmail: EventEmitter<Participant[]> = new EventEmitter<
    Participant[]
  >();
  addParticipantEmail = {} as Participant;
  addparticipantEmailArray: Participant[] = [];
  @Input() siteIdAddEmail = '';
  model: AddEmail;
  constructor(
    private readonly siteDetailedService: SiteDetailsService,
    private readonly toastr: ToastrService,
  ) {
    super();
    this.model = {email: ''};
  }

  addParticipant(): void {
    this.subs.add(
      this.siteDetailedService
        .addParticipants(this.siteIdAddEmail, this.model)
        .subscribe(
          (successResponse: AddEmailResponse) => {
            this.addParticipantEmail.id = successResponse.participantId;
            this.addParticipantEmail.email = this.model.email;
            this.addparticipantEmailArray.push(this.addParticipantEmail);
            if (getMessage(successResponse.code)) {
              this.toastr.success(getMessage(successResponse.code));
            } else {
              this.toastr.success(successResponse.message);
            }
            console.log(this.addparticipantEmailArray);
            this.addEmail.emit(this.addparticipantEmailArray);
          },
          (error) => {
            this.toastr.error(error);
          },
        ),
    );
  }
  onCancel(): void {
    this.cancel.emit();
  }
}
