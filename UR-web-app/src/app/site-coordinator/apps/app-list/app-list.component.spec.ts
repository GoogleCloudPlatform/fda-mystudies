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
import {EntityService} from '../../../service/entity.service';
import {of} from 'rxjs';
import {BsModalService, BsModalRef, ModalModule} from 'ngx-bootstrap/modal';

import {AppsModule} from '../apps.module';
import {AppsService} from '../shared/apps.service';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {expectedAppList} from 'src/app/entity/mock-apps-data';

import {AppListComponent} from './app-list.component';
import {ManageApps} from '../shared/app.model';

describe('AppListComponent', () => {
  let component: AppListComponent;
  let fixture: ComponentFixture<AppListComponent>;

  beforeEach(async(async () => {
    const appsServiceSpy = jasmine.createSpyObj<AppsService>('AppsService', {
      getUserApps: of(expectedAppList),
    });
    await TestBed.configureTestingModule({
      declarations: [AppListComponent],
      imports: [
        SiteCoordinatorModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
        AppsModule,
        HttpClientModule,
        RouterTestingModule.withRoutes([]),
        ModalModule.forRoot(),
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
        {provide: AppsService, useValue: appsServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(AppListComponent);
        component = fixture.componentInstance;
      });
  }));
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should NOT have apps before ngOnInit', () => {
    component.manageApp$.subscribe((manageApps: ManageApps) => {
      expect(manageApps.apps.length).toBe(
        0,
        'should not have apps before ngOnInit',
      );
    });
  });

  describe('after get apps', () => {
    beforeEach(async(() => {
      fixture.detectChanges();
      void fixture.whenStable();
    }));

    it('should get the apps List via refresh function', fakeAsync(() => {
      component.manageApp$.subscribe((manageApps: ManageApps) => {
        expect(manageApps.apps.length).toEqual(expectedAppList.apps.length);
      });
    }));
  });
});
