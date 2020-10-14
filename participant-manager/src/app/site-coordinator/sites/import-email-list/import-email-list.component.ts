import {Component, Output, Input, EventEmitter} from '@angular/core';
import {SiteDetailsService} from '../shared/site-details.service';
import {ImportParticipantEmailResponse} from '../shared/import-particpants';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-import-email-list',
  templateUrl: './import-email-list.component.html',
  styleUrls: ['./import-email-list.component.scss'],
})
export class ImportEmailListComponent extends UnsubscribeOnDestroyAdapter {
  @Output() cancel = new EventEmitter();
  @Output() import: EventEmitter<
    ImportParticipantEmailResponse
  > = new EventEmitter<ImportParticipantEmailResponse>();
  @Input() siteIdImportEmail = '';
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
    const selectedFile: File = (target.files as FileList)[0];
    this.file = selectedFile;
    this.fileName = this.file.name;
  }

  cancelled(): void {
    this.cancel.emit();
  }

  importParticipants(): void {
    if (this.file?.name) {
      const formData = new FormData();
      formData.append('file', this.file, this.file.name);
      this.siteDetailsService
        .importParticipants(this.siteIdImportEmail, formData)
        .subscribe(
          (successResponse: ImportParticipantEmailResponse) => {
            console.log(successResponse);
            if (getMessage(successResponse.code)) {
              this.toastr.success(getMessage(successResponse.code));
            } else {
              this.toastr.success(successResponse.message);
            }
            this.import.emit(successResponse);
          },
          (error) => {
            this.toastr.error(error);
          },
        );
    } else {
      this.toastr.error('Please select a file to upload.');
    }
  }
}
