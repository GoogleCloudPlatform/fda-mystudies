import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {AccountProfileComponent} from './account-profile.component';
import {EntityService} from 'src/app/service/entity.service';
import {AccountService} from '../shared/account.service';
import {of} from 'rxjs';
import {AccountModule} from '../account.module';
import {ToastrModule} from 'ngx-toastr';
import {SiteCoordinatorModule} from '../../../site-coordinator/site-coordinator.module';
import {RouterTestingModule} from '@angular/router/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';
import {
  expectedProfiledataResposnse,
  expectedUpdateResponse,
} from '../../../entity/mock-profile-data';
describe('ChangePasswordComponent', () => {
  let component: AccountProfileComponent;
  let fixture: ComponentFixture<AccountProfileComponent>;

  beforeEach(async(async () => {
    const accountServiceSpy = jasmine.createSpyObj<AccountService>(
      'AccountService',
      {
        fetchProfile: of(expectedProfiledataResposnse),
        updateUserProfile: of(expectedUpdateResponse),
      },
    );
    await TestBed.configureTestingModule({
      declarations: [AccountProfileComponent],
      imports: [
        AccountModule,
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        SiteCoordinatorModule,
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [
        EntityService,
        {provide: AccountService, useValue: accountServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(AccountProfileComponent);
        component = fixture.componentInstance;
      });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should update the profile when  button is submitted', fakeAsync(async () => {
    spyOn(component, 'updateProfile').and.callThrough();
    component.updateProfile();
    expect(component.profileForm.invalid).toBe(false);
    fixture.detectChanges();
    tick(10000);
    await fixture.whenStable();
    // eslint-disable-next-line @typescript-eslint/unbound-method
    expect(component.updateProfile).toHaveBeenCalled();
  }));
  it('should cancel the profile when  cancel is clicked', fakeAsync(async () => {
    spyOn(component, 'cancel').and.callThrough();
    component.cancel();
    fixture.detectChanges();
    tick(10000);
    await fixture.whenStable();
    // eslint-disable-next-line @typescript-eslint/unbound-method
    expect(component.cancel).toHaveBeenCalled();
  }));
});
