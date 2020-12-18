import {
  async,
  TestBed,
  ComponentFixture,
  fakeAsync,
} from '@angular/core/testing';
import {ModalModule, BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {ToastrModule} from 'ngx-toastr';
import {EntityService} from '../../../service/entity.service';
import {of} from 'rxjs';
import {AppDetailsService} from '../shared/app-details.service';
import {AppDetailsComponent} from './app-details.component';
import {expectedAppDetails} from '../../../entity/mock-apps-data';
import {AppsModule} from '../apps.module';

describe('AppDetailsComponent', () => {
  let component: AppDetailsComponent;
  let fixture: ComponentFixture<AppDetailsComponent>;

  beforeEach(async(async () => {
    const appDetailsServiceSpy = jasmine.createSpyObj<AppDetailsService>(
      'AppDetailsService',
      {get: of(expectedAppDetails)},
    );

    await TestBed.configureTestingModule({
      declarations: [AppDetailsComponent],
      imports: [
        RouterTestingModule,
        ModalModule.forRoot(),
        BrowserAnimationsModule,
        AppsModule,
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [
        EntityService,
        BsModalService,
        BsModalRef,
        {provide: AppDetailsService, useValue: appDetailsServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(AppDetailsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get apps participants details via refresh function', fakeAsync(() => {
    component.appDetail$.subscribe((appDetail) => {
      expect(appDetail.participants.length).toEqual(
        expectedAppDetails.participants.length,
      );
    });
  }));

  it('should get apps participants enrolledStudies details', fakeAsync(() => {
    component.search('mockittest@grr.la');
    fixture.detectChanges();
    component.appDetail$.subscribe((appDetail) => {
      expect(appDetail.participants.length).toBe(
        expectedAppDetails.participants.length,
      );
    });
  }));
});
