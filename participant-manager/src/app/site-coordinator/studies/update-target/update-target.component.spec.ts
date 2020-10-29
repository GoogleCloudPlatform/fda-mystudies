import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastrModule} from 'ngx-toastr';
import {EntityService} from 'src/app/service/entity.service';

import {UpdateTargetComponent} from './update-target.component';
import {HttpClientModule} from '@angular/common/http';
import {StudiesModule} from '../studies.module';
import {StudyDetailsService} from '../shared/study-details.service';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import * as expectedResult from 'src/app/entity/mock-studies-data';
import {of} from 'rxjs';

describe('UpdateTargetComponent', () => {
  let component: UpdateTargetComponent;
  let fixture: ComponentFixture<UpdateTargetComponent>;
  let updateTarget: DebugElement;
  let cancel: DebugElement;
  let targetInput: DebugElement;
  beforeEach(async(async () => {
    const studiesDetailsServiceSpy = jasmine.createSpyObj<StudyDetailsService>(
      'StudyDetailsService',
      {updateTargetEnrollment: of(expectedResult.expectedResponse)},
    );
    await TestBed.configureTestingModule({
      declarations: [UpdateTargetComponent],
      imports: [
        StudiesModule,
        BrowserAnimationsModule,
        HttpClientModule,
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [
        EntityService,
        {provide: StudyDetailsService, useValue: studiesDetailsServiceSpy},
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateTargetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    updateTarget = fixture.debugElement.query(By.css('[name="update"]'));
    targetInput = fixture.debugElement.query(By.css('[name="target"]'));
    cancel = fixture.debugElement.query(By.css('[name="cancel"]'));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not show a validation error if the input field are auto filled', () => {
    const targetInputs = targetInput.nativeElement as HTMLInputElement;
    targetInputs.valueAsNumber =
      expectedResult.expectedTargetEnrollment.targetEnrollment;
    fixture.detectChanges();
    const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
    const errorHelpBlock = fixture.debugElement.query(
      By.css('.help-block.with-errors'),
    );
    expect(errorHelpBlock).toBeFalsy();
    expect(errorMsg).toBeFalsy();
  });

  it('should update the target enrollment when clicked', fakeAsync(async () => {
    const updateSpy = spyOn(component, 'update');
    component.updateTargetEnrollmentRequest =
      expectedResult.expectedTargetEnrollment;
    const updateButton = updateTarget.nativeElement as HTMLInputElement;
    const targetInputs = targetInput.nativeElement as HTMLInputElement;
    targetInputs.valueAsNumber =
      expectedResult.expectedTargetEnrollment.targetEnrollment;
    dispatchEvent(new Event('input'));
    fixture.detectChanges();
    tick();
    updateButton.click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(updateSpy).toHaveBeenCalledTimes(1);
    expect(component.updateTargetEnrollmentRequest.targetEnrollment).toEqual(
      expectedResult.expectedTargetEnrollment.targetEnrollment,
    );
  }));

  it('should close model on click of cancel button', fakeAsync(() => {
    const closeModalSpy = spyOn(component, 'closeModal');
    const cancelButton = cancel.nativeElement as HTMLInputElement;
    cancelButton.click();
    expect(closeModalSpy).toHaveBeenCalledTimes(1);
  }));
});
