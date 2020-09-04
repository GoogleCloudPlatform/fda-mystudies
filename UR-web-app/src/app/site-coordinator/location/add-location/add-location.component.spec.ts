import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {AddLocationComponent} from './add-location.component';
import {LocationService} from '../shared/location.service';
import {ToastrModule, ToastrService} from 'ngx-toastr';
import {BsModalService, ModalModule} from 'ngx-bootstrap/modal';
import {RouterTestingModule} from '@angular/router/testing';
import {LocationModule} from '../location.module';
import {FormsModule, NgForm} from '@angular/forms';
import {EntityService} from 'src/app/service/entity.service';
import {HttpClientModule} from '@angular/common/http';
import {By} from '@angular/platform-browser';
import {of} from 'rxjs';
import {Location} from '../shared/location.model';
describe('AddLocationComponent', () => {
  let component: AddLocationComponent;
  let fixture: ComponentFixture<AddLocationComponent>;
  let submitLocation: DebugElement;
  let customIdInput: DebugElement;
  let nameInput: DebugElement;
  let descriptionInput: DebugElement;
  let locationsServiceSpy: jasmine.SpyObj<LocationService>;
  beforeEach(async(() => {
    locationsServiceSpy = jasmine.createSpyObj<LocationService>(
      'LocationService',
      ['addLocation'],
    );
    TestBed.configureTestingModule({
      declarations: [AddLocationComponent],
      imports: [
        ModalModule.forRoot(),
        RouterTestingModule,
        LocationModule,
        HttpClientModule,
        FormsModule,
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [
        NgForm,
        EntityService,
        ToastrService,
        BsModalService,
        {
          provide: LocationService,
          useValue: locationsServiceSpy,
          useClass: locationsServiceSpy,
        },
      ],
    });
  }));
  beforeEach(async(() => {
    fixture = TestBed.createComponent(AddLocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    submitLocation = fixture.debugElement.query(By.css('[name="add"]'));
    customIdInput = fixture.debugElement.query(By.css('[name="customId"]'));
    nameInput = fixture.debugElement.query(By.css('[name="name"]'));
    descriptionInput = fixture.debugElement.query(
      By.css('[name="description"]'),
    );
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should not show a validation error if the input field is filled', () => {
    const customIdInputs = customIdInput.nativeElement as HTMLInputElement;
    const nameInputs = nameInput.nativeElement as HTMLInputElement;
    const descriptionInputs = descriptionInput.nativeElement as HTMLInputElement;
    customIdInputs.value = 'customid3';
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

  it('should show  validation error if the input field is empty', async () => {
    const customIdInputs = customIdInput.nativeElement as HTMLInputElement;
    customIdInputs.value = '';
    customIdInputs.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
    const errorHelpBlock = fixture.debugElement.query(By.css('.with-errors'));
    expect(customIdInputs.required).toBeTrue();
    expect(errorHelpBlock).toBeTruthy();
    expect(errorMsg).toBeTruthy();
  });

  it('should show  validation error if the input field exceeds max charecter', async () => {
    const customIdInputs = customIdInput.nativeElement as HTMLInputElement;
    customIdInputs.value = 'checking max charecter';
    customIdInputs.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
    const errorHelpBlock = fixture.debugElement.query(
      By.css('.help-block.with-errors'),
    );
    expect(errorHelpBlock).toBeTruthy();
    expect(errorMsg).toBeTruthy();
  });

  it('should check form submit button is disabled when loaded', () => {
    const submitButton = submitLocation.nativeElement as HTMLInputElement;
    component.enabled = false;
    fixture.detectChanges();
    expect(submitButton.disabled).toBeTruthy();
  });

  it('should set the form value before submit and expect same', fakeAsync(async () => {
    const expectedResponse = {
      locationId: '0',
      customId: 'customid3',
      name: 'Location Name',
      description: `A location description includes the location details and related information so that the user is able to understand more about the location. The description gives the user an idea of the location or explain the location details.`,
      status: 1,
    } as Location;
    locationsServiceSpy.addLocation.and.returnValue(of(expectedResponse));
    fixture.componentInstance.location.customId = 'customid3';
    fixture.componentInstance.location.name = 'Location Name';
    fixture.componentInstance.location.description = `A location description includes the location details and related information so that the user is able to understand more about the location. The description gives the user an idea of the location or explain the location details.`;
    await fixture.whenStable();
    const submitButton = submitLocation.nativeElement as HTMLInputElement;
    const customIdInputs = customIdInput.nativeElement as HTMLInputElement;
    const nameInputs = nameInput.nativeElement as HTMLInputElement;
    const descriptionInputs = descriptionInput.nativeElement as HTMLInputElement;
    customIdInputs.value = 'customid3';
    nameInputs.value = 'Location Name';
    descriptionInputs.value = `A location description includes the location details and related information so that the user is able to understand more about the location. The description gives the user an idea of the location or explain the location details.`;
    dispatchEvent(new Event('input'));
    fixture.detectChanges();
    tick();
    submitButton.click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(component.location.customId).toEqual('customid3');
    expect(component.location.name).toEqual('Location Name');
    expect(component.location.description).toEqual(
      'A location description includes the location details and related information so that the user is able to understand more about the location. The description gives the user an idea of the location or explain the location details.',
    );
  }));
  it('should click on submit button and check service is called', fakeAsync(() => {
    const submitSpy = spyOn(component, 'addLocation');
    const submitButton = submitLocation.nativeElement as HTMLInputElement;
    submitButton.click();
    expect(submitSpy).toHaveBeenCalledTimes(1);
  }));
});
