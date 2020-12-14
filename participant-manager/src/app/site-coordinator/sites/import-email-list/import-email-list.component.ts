import {Component, Output, Input, EventEmitter} from '@angular/core';
import {SiteDetailsService} from '../shared/site-details.service';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {ToastrService} from 'ngx-toastr';
import {ImportParticipantEmailResponse} from '../shared/import-participants';

@Component({
  selector: 'app-import-email-list',
  templateUrl: './import-email-list.component.html',
  styleUrls: ['./import-email-list.component.scss'],
})
export class ImportEmailListComponent extends UnsubscribeOnDestroyAdapter {
  @Output() cancel = new EventEmitter();
  @Output()
  import: EventEmitter<ImportParticipantEmailResponse> = new EventEmitter<
    ImportParticipantEmailResponse
  >();
  @Input() siteId = '';
  fileName = '';
  file?: File;
  constructor(
    private readonly siteDetailsService: SiteDetailsService,
    private readonly toastr: ToastrService,
  ) {
    super();
  }
  fileChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files?.length) {
      const selectedFile: File = target.files[0];

      if (
        selectedFile.type ===
          'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
        selectedFile.type === 'application/vnd.ms-excel'
      ) {
        this.file = selectedFile;
        this.fileName = this.file.name;
      } else {
        this.toastr.error('Please upload a .xls or .xlsx file');
      }
    } else {
      this.fileName = '';
    }
  }

  cancelled(): void {
    this.cancel.emit();
  }

  importParticipants(): void {
    if (this.file?.name) {
      const formData = new FormData();
      formData.append('file', this.file, this.file.name);
      this.siteDetailsService
        .importParticipants(this.siteId, formData)
        .subscribe((successResponse: ImportParticipantEmailResponse) => {
          if (
            successResponse.invalidEmails.length > 0 ||
            successResponse.duplicateEmails.length > 0
          ) {
            this.toastr.error(
              `The email list was imported with the following issues: <br/>` +
                String(
                  successResponse.invalidEmails.length +
                    successResponse.duplicateEmails.length,
                ) +
                ` emails failed to import.</br>` +
                `Reason for import failure for these could be one of the following:<br/>
                1.Email not in proper format <br/>
2.Duplicate emails exist in the list <br/>
3.Participant enabled in another site within the same study<br/>
4.Email already exists in the site<br/>
5. The email already exists in enabled state for another site in the same study.
                `,
            );
          }
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else {
            this.toastr.success(successResponse.message);
          }
          this.import.emit(successResponse);
        });
    } else {
      this.toastr.error('Please select a file to upload.');
    }
  }
}
