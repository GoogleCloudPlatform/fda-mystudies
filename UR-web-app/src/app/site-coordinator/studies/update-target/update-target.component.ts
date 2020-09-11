import {Component, Input, EventEmitter, Output} from '@angular/core';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {ToastrService} from 'ngx-toastr';
import {StudyDetailsService} from '../shared/study-details.service';
import {UpdateTargetEnrollmentRequest} from '../shared/site.model';

@Component({
  selector: 'update-target',
  templateUrl: './update-target.component.html',
})
export class UpdateTargetComponent {
  @Input() studyId = '';
  @Input() targetEnrollment = 0;
  @Output() closeModalEvent = new EventEmitter<number>();

  updateTargetEnrollmentRequest: UpdateTargetEnrollmentRequest = {
    targetEnrollment: 0,
  };
  constructor(
    private readonly toastr: ToastrService,
    private readonly studyDetailsService: StudyDetailsService,
  ) {}

  ngOnChanges(): void {
    this.updateTargetEnrollmentRequest = {
      targetEnrollment: this.targetEnrollment,
    };
  }

  update(): void {
    this.studyDetailsService
      .updateTargetEnrollment(this.updateTargetEnrollmentRequest, this.studyId)
      .subscribe(
        (successResponse: ApiResponse) => {
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else {
            this.toastr.success('Success');
          }
          this.closeModal(this.updateTargetEnrollmentRequest.targetEnrollment);
        },
        () => {
          this.closeModal(this.targetEnrollment);
        },
      );
  }

  closeModal(targetEnrollment: number): void {
    this.closeModalEvent.next(targetEnrollment);
  }
}
