import {
  async,
  TestBed,
  ComponentFixture,
  fakeAsync,
} from '@angular/core/testing';
import {
  BrowserAnimationsModule,
  NoopAnimationsModule,
} from '@angular/platform-browser/animations';

import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';
import {ToastrModule} from 'ngx-toastr';
import {of} from 'rxjs';
import {AddNewUserComponent} from './new-user.component';
import {UserModule} from '../user.module';
import {UserService} from '../shared/user.service';
import {
  expectedAppDetails,
  addUserResponse,
  addUserRequest,
} from 'src/app/entity/mock-app-details';
import {AppDetails} from '../shared/app-details';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {Permission} from 'src/app/shared/permission-enums';
import {AppsService} from '../../apps/shared/apps.service';
describe('AddNewUserComponent', () => {
  let component: AddNewUserComponent;
  let fixture: ComponentFixture<AddNewUserComponent>;
  let submit: DebugElement;
  let firstNameInput: DebugElement;
  let lastNameInput: DebugElement;
  let emailInput: DebugElement;
  let superAdminInput: DebugElement;

  beforeEach(async(async () => {
    const userServiceSpy = jasmine.createSpyObj<UserService>('UserService', {
      add: of(addUserResponse),
    });
    const appServiceSpy = jasmine.createSpyObj<AppsService>('AppsService', {
      getAllAppsWithStudiesAndSites: of(expectedAppDetails),
    });
    await TestBed.configureTestingModule({
      declarations: [AddNewUserComponent],
      imports: [
        UserModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
        HttpClientModule,
        RouterTestingModule.withRoutes([]),
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [
        {provide: UserService, useValue: userServiceSpy},
        {provide: AppsService, useValue: appServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(AddNewUserComponent);
        component = fixture.componentInstance;
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should NOT have apps before ngOnInit', () => {
    const emptyAppDetails = {} as AppDetails;
    expect(component.appDetails).toEqual(
      emptyAppDetails,
      'should not have apps before ngOnInit',
    );
  });
  describe('after get apps', () => {
    beforeEach(async(() => {
      fixture.detectChanges();
      void fixture.whenStable();
    }));

    it('should get the apps List via refresh function', fakeAsync(() => {
      expect(component.appDetails.apps.length).toEqual(
        expectedAppDetails.apps.length,
      );
    }));
  });
  describe('add user', () => {
    beforeEach(async(() => {
      fixture = TestBed.createComponent(AddNewUserComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      submit = fixture.debugElement.query(By.css('[name="add"]'));
      firstNameInput = fixture.debugElement.query(By.css('[name="firstName"]'));
      lastNameInput = fixture.debugElement.query(By.css('[name="lastName"]'));
      emailInput = fixture.debugElement.query(By.css('[name="emailText"]'));
      superAdminInput = fixture.debugElement.query(
        By.css('[name="superAdminCheckBox"]'),
      );
      fixture.detectChanges();
    }));

    it('should not show a validation error if the input field is filled', () => {
      const firstNameInputs = firstNameInput.nativeElement as HTMLInputElement;
      const lastNameInputs = lastNameInput.nativeElement as HTMLInputElement;
      const emailInputs = emailInput.nativeElement as HTMLInputElement;
      firstNameInputs.value = 'super';
      lastNameInputs.value = 'admin';
      emailInputs.value = 'super@grr.la';
      fixture.detectChanges();
      const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
      const errorHelpBlock = fixture.debugElement.query(
        By.css('.help-block.with-errors'),
      );
      expect(errorHelpBlock).toBeFalsy();
      expect(errorMsg).toBeFalsy();
    });

    it('should show  validation error if the input field is empty', async () => {
      const firstNameInputs = firstNameInput.nativeElement as HTMLInputElement;
      firstNameInputs.value = '';
      firstNameInputs.dispatchEvent(new Event('input'));
      fixture.detectChanges();
      await fixture.whenStable();
      const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
      expect(firstNameInputs.required).toBeTrue();
      expect(errorMsg).toBeTruthy();
    });

    it('should check form submit button is disabled when loaded', () => {
      const submitButton = submit.nativeElement as HTMLInputElement;
      fixture.detectChanges();
      expect(submitButton.disabled).toBeTruthy();
    });

    it('should set the form value before submit and expect same', fakeAsync(async () => {
      const addSpy = spyOn(component, 'add');
      component.user = addUserRequest;
      const submitButton = submit.nativeElement as HTMLInputElement;
      const firstNameInputs = firstNameInput.nativeElement as HTMLInputElement;
      const lastNameInputs = lastNameInput.nativeElement as HTMLInputElement;
      const emailInputs = emailInput.nativeElement as HTMLInputElement;
      const superAdminInputs = superAdminInput.nativeElement as HTMLInputElement;
      await fixture.whenStable();

      firstNameInputs.value = addUserRequest.firstName;
      lastNameInputs.value = addUserRequest.lastName;
      emailInputs.value = addUserRequest.email;
      superAdminInputs.checked;
      dispatchEvent(new Event('input'));
      submitButton.click();
      expect(addSpy).toHaveBeenCalledTimes(1);
      expect(component.user.firstName).toEqual(addUserRequest.firstName);
      expect(component.user.lastName).toEqual(addUserRequest.lastName);
      expect(component.user.email).toEqual(addUserRequest.email);
      expect(component.user.superAdmin).toEqual(addUserRequest.superAdmin);
    }));

    it('should handle app checkbox is selected', () => {
      const mockApp = expectedAppDetails.apps[0];
      mockApp.selected = true;
      component.appCheckBoxChange(mockApp);
      expect(component.appDetails.apps[0].permission).toEqual(Permission.View);
      expect(component.appDetails.apps[0].selectedSitesCount).toEqual(
        mockApp.totalSitesCount,
      );
      component.appDetails.apps[0].studies.forEach((study) => {
        expect(study.permission).toEqual(Permission.View);
        expect(study.selected).toEqual(true);
        study.sites.forEach((site) => {
          expect(site.permission).toEqual(Permission.View);
          expect(site.selected).toEqual(true);
        });
      });
    });

    it('should handle app checkbox is not selected', () => {
      const mockApp = expectedAppDetails.apps[0];
      mockApp.selected = false;
      component.appCheckBoxChange(mockApp);
      expect(component.appDetails.apps[0].permission).toEqual(null);
      expect(component.appDetails.apps[0].selectedSitesCount).toEqual(0);
      component.appDetails.apps[0].studies.forEach((study) => {
        expect(study.permission).toEqual(null);
        expect(study.selected).toEqual(false);
        study.sites.forEach((site) => {
          expect(site.permission).toEqual(null);
          expect(site.selected).toEqual(false);
        });
      });
    });

    it('should handle app view permission radio ', () => {
      const mockApp = expectedAppDetails.apps[0]; // use or create a mock app object
      mockApp.permission = Permission.View;
      component.appRadioButtonChange(mockApp);
      expect(component.appDetails.apps[0].permission).toEqual(Permission.View);
      component.appDetails.apps[0].studies.forEach((study) => {
        expect(study.permission).toEqual(Permission.View);
        study.sites.forEach((site) => {
          expect(site.permission).toEqual(Permission.View);
        });
      });
    });

    it('should handle app view and edit permission radio ', () => {
      const mockApp = expectedAppDetails.apps[0]; // use or create a mock app object
      mockApp.permission = Permission.ViewAndEdit;
      component.appRadioButtonChange(mockApp);
      expect(component.appDetails.apps[0].permission).toEqual(
        Permission.ViewAndEdit,
      );
      component.appDetails.apps[0].studies.forEach((study) => {
        expect(study.permission).toEqual(Permission.ViewAndEdit);
        study.sites.forEach((site) => {
          expect(site.permission).toEqual(Permission.ViewAndEdit);
        });
      });
    });

    it('should handle study checkbox is selected', () => {
      const mockApp = expectedAppDetails.apps[0];
      const mockStudy = expectedAppDetails.apps[0].studies[0];
      mockStudy.selected = true;
      component.studyCheckBoxChange(mockStudy, mockApp);

      expect(component.appDetails.apps[0].studies[0].permission).toEqual(
        Permission.View,
      );
      expect(
        component.appDetails.apps[0].studies[0].selectedSitesCount,
      ).toEqual(mockStudy.sites.length);
      component.appDetails.apps[0].studies[0].sites.forEach((site) => {
        expect(site.permission).toEqual(Permission.View);
      });
    });

    it('should handle study checkbox is not selected', () => {
      const mockApp = expectedAppDetails.apps[0];
      const mockStudy = expectedAppDetails.apps[0].studies[0];
      mockStudy.selected = false;
      component.studyCheckBoxChange(mockStudy, mockApp);
      expect(component.appDetails.apps[0].studies[0].permission).toEqual(null);
      expect(
        component.appDetails.apps[0].studies[0].selectedSitesCount,
      ).toEqual(0);
      component.appDetails.apps[0].studies[0].sites.forEach((site) => {
        expect(site.permission).toEqual(null);
        expect(site.selected).toEqual(false);
      });
    });

    it('should handle study view permission radio ', () => {
      const mockStudy = expectedAppDetails.apps[0].studies[0];
      mockStudy.permission = Permission.View;
      component.studyRadioButtonChange(mockStudy);
      expect(component.appDetails.apps[0].studies[0].permission).toEqual(
        Permission.View,
      );
      component.appDetails.apps[0].studies[0].sites.forEach((site) => {
        expect(site.permission).toEqual(Permission.View);
      });
    });

    it('should handle study view and edit permission radio ', () => {
      const mockStudy = expectedAppDetails.apps[0].studies[0];
      mockStudy.permission = Permission.ViewAndEdit;
      component.studyRadioButtonChange(mockStudy);
      expect(component.appDetails.apps[0].studies[0].permission).toEqual(
        Permission.ViewAndEdit,
      );
      component.appDetails.apps[0].studies[0].sites.forEach((site) => {
        expect(site.permission).toEqual(Permission.ViewAndEdit);
      });
    });

    it('should handle site checkbox is selected', () => {
      const mockApp = expectedAppDetails.apps[0];
      const mockStudy = expectedAppDetails.apps[0].studies[0];
      const mockSite = expectedAppDetails.apps[0].studies[0].sites[0];
      mockSite.selected = true;
      component.siteCheckBoxChange(mockSite, mockStudy, mockApp);
      expect(
        component.appDetails.apps[0].studies[0].sites[0].permission,
      ).toEqual(Permission.View);
    });

    it('should handle study checkbox is not selected', () => {
      const mockApp = expectedAppDetails.apps[0];
      const mockStudy = expectedAppDetails.apps[0].studies[0];
      const mockSite = expectedAppDetails.apps[0].studies[0].sites[0];
      mockSite.selected = false;
      component.siteCheckBoxChange(mockSite, mockStudy, mockApp);

      expect(
        component.appDetails.apps[0].studies[0].sites[0].permission,
      ).toEqual(null);
    });

    it('should handle location checkbox is selected', () => {
      component.user = addUserRequest;
      component.user.manageLocationsSelected = true;
      component.locationsCheckBoxChange();
      expect(component.user.manageLocations).toEqual(Permission.View);
    });

    it('should handle location checkbox is not selected', () => {
      component.user = addUserRequest;
      component.user.manageLocationsSelected = false;
      component.locationsCheckBoxChange();
      expect(component.user.manageLocations).toEqual(null);
    });
  });
});
