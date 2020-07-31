import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';

import {EditLocationComponent} from './edit-location.component';
import {ModalModule} from 'ngx-bootstrap/modal';
import {RouterTestingModule} from '@angular/router/testing';
import {LocationModule} from '../location.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';
import {ToastrModule} from 'ngx-toastr';
import {EntityService} from 'src/app/service/entity.service';
import {LocationService} from '../shared/location.service';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {updatedLocation} from 'src/app/entity/mockLocationData';

describe('EditLocationComponent', () => {
  let component: EditLocationComponent;
  let fixture: ComponentFixture<EditLocationComponent>;
  let updateLocation: DebugElement;
  let cancelUpdate: DebugElement;
  let statusUpdate: DebugElement;
  let nameInput: DebugElement;
  let descriptionInput: DebugElement;
  beforeEach(async(async () => {
    const locationServiceSpy = jasmine.createSpyObj<LocationService>(
      'LocationService',
      ['update'],
    );
    await TestBed.configureTestingModule({
      declarations: [EditLocationComponent],
      imports: [
        ModalModule.forRoot(),
        RouterTestingModule,
        LocationModule,
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
        {provide: LocationService, useValue: locationServiceSpy},
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditLocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    updateLocation = fixture.debugElement.query(By.css('[name="update"]'));
    cancelUpdate = fixture.debugElement.query(By.css('[name="cancel"]'));
    statusUpdate = fixture.debugElement.query(By.css('[name="status"]'));
    nameInput = fixture.debugElement.query(By.css('[name="name"]'));
    descriptionInput = fixture.debugElement.query(
      By.css('[name="description"]'),
    );
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not show a validation error if the input field are auto filled', () => {
    const nameInputs = nameInput.nativeElement as HTMLInputElement;
    const descriptionInputs = descriptionInput.nativeElement as HTMLInputElement;
    nameInputs.value = 'Location Name';
    descriptionInputs.value = 'Testing description value';
    fixture.detectChanges();
    const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
    const errorHelpBlock = fixture.debugElement.query(
      By.css('.help-block.with-errors'),
    );
    expect(errorHelpBlock).toBeFalsy();
    expect(errorMsg).toBeFalsy();
  });

  it('should update the location when update button is clicked', fakeAsync(async () => {
    component.location = updatedLocation;
    const updateButton = updateLocation.nativeElement as HTMLInputElement;
    const nameInputs = nameInput.nativeElement as HTMLInputElement;
    const descriptionInputs = descriptionInput.nativeElement as HTMLInputElement;
    nameInputs.value = updatedLocation.name;
    descriptionInputs.value = updatedLocation.description;
    dispatchEvent(new Event('input'));
    fixture.detectChanges();
    tick();
    updateButton.click();
    fixture.detectChanges();
    spyOn(component, 'updateLocation').and.callThrough();
    await fixture.whenStable();
    expect(component.location.name).toEqual(updatedLocation.name);
    expect(component.location.description).toEqual(updatedLocation.description);
  }));

  it('should update the location when the update button is clicked', fakeAsync(() => {
    const updateSpy = spyOn(component, 'update');
    const updateButton = updateLocation.nativeElement as HTMLInputElement;
    updateButton.click();
    expect(updateSpy).toHaveBeenCalledTimes(1);
  }));
  it('should cancel the update location when cancel button is clicked', fakeAsync(() => {
    const cancelSpy = spyOn(component, 'closeModal');
    const cancelButton = cancelUpdate.nativeElement as HTMLInputElement;
    cancelButton.click();
    expect(cancelSpy).toHaveBeenCalledTimes(1);
  }));
  it('should activate/deactivate site when decommission/reactivate is clicked', fakeAsync(() => {
    const statusChangeSpy = spyOn(component, 'changeStatus');
    const deactivateButton = statusUpdate.nativeElement as HTMLInputElement;
    deactivateButton.click();
    expect(statusChangeSpy).toHaveBeenCalledTimes(1);
  }));
});
