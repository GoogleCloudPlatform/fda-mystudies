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
import {ToastrModule} from 'ngx-toastr';
import {BsModalService, ModalModule} from 'ngx-bootstrap/modal';
import {RouterTestingModule} from '@angular/router/testing';
import {LocationModule} from '../location.module';
import {FormsModule, NgForm} from '@angular/forms';
import {EntityService} from 'src/app/service/entity.service';
import {HttpClientModule} from '@angular/common/http';
import {By} from '@angular/platform-browser';

describe('AddLocationComponent', () => {
  let component: AddLocationComponent;
  let fixture: ComponentFixture<AddLocationComponent>;
  let submitLocation: DebugElement;
  let customIdInput: DebugElement;
  let nameInput: DebugElement;
  let descriptionInput: DebugElement;

  beforeEach(async(() => {
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
      providers: [NgForm, EntityService, LocationService, BsModalService],
    });
  }));

  beforeEach(async(() => {
    fixture = TestBed.createComponent(AddLocationComponent);
    component = fixture.componentInstance;

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
    component.location.customId = 'customid3';
    component.location.name = 'Location Name';
    component.location.description = 'This is location Description';
    fixture.detectChanges();
    const Errormsg = fixture.debugElement.query(By.css('.validation-error'));
    expect(Errormsg).toBeFalsy();
  });

  it('Locations value to input properties on form load', () => {
    const submitButton = submitLocation.nativeElement as HTMLInputElement;
    component.enabled = false;
    fixture.detectChanges();
    expect(submitButton.disabled).toBeTruthy();
  });

  it('Entering value in input controls and emit output events', fakeAsync(() => {
    fixture.componentInstance.location.customId = 'customid3';
    fixture.componentInstance.location.name = 'Location Name';
    fixture.componentInstance.location.description =
      'This is location Description';
    void fixture.whenStable().then(() => {
      const submitButton = submitLocation.nativeElement as HTMLInputElement;
      const customIdInputs = customIdInput.nativeElement as HTMLInputElement;
      const nameInputs = nameInput.nativeElement as HTMLInputElement;
      const descriptionInputs = descriptionInput.nativeElement as HTMLInputElement;
      customIdInputs.value = 'customid3';
      nameInputs.value = 'Location Name';
      descriptionInputs.value = 'This is location Description';
      dispatchEvent(new Event('input'));
      fixture.detectChanges();
      tick();
      submitButton.click();
      fixture.detectChanges();
      expect(component.location.customId).toEqual('customid3');
      expect(component.location.name).toEqual('Location Name');
      expect(component.location.description).toEqual(
        'This is location Description',
      );
    });
  }));

  it('should reset the form submit state when reset button is clicked', fakeAsync(() => {
    const forms = fixture.debugElement.children[0].injector.get(NgForm);
    const formEl = fixture.debugElement.query(By.css('form'));

    dispatchEvent(new Event(formEl.nativeElement));
    fixture.detectChanges();
    tick();
    expect(forms.valid).toBe(true);

    dispatchEvent(new Event(formEl.nativeElement));
    fixture.detectChanges();
    tick();
    expect(forms.valid).toBe(true);
  }));
});
