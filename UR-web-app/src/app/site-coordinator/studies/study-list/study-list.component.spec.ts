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
import {StudyListComponent} from './study-list.component';

import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';
import {ToastrModule} from 'ngx-toastr';
import {EntityService} from '../../../service/entity.service';
import {of, Observable} from 'rxjs';
import {BsModalService, BsModalRef, ModalModule} from 'ngx-bootstrap/modal';

import {StudiesModule} from '../studies.module';
import {StudiesService} from '../shared/studies.service';
import {DashboardModel} from '../shared/dashboard.model';
import {SiteCoordinatorModule} from '../../site-coordinator.module';

describe('StudyListComponent', () => {
  let component: StudyListComponent;
  let fixture: ComponentFixture<StudyListComponent>;

  beforeEach(async(async () => {
    const studyServiceSpy = jasmine.createSpyObj<StudiesService>(
      'StudiesService',
      ['getStudies'],
    );
    await TestBed.configureTestingModule({
      declarations: [StudyListComponent],
      imports: [
        SiteCoordinatorModule,
        BrowserAnimationsModule,
        StudiesModule,
        NoopAnimationsModule,
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
        {provide: StudiesService, useValue: studyServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(StudyListComponent);
        component = fixture.componentInstance;
        const expectedStudyList: Observable<DashboardModel[]> = of([
          {
            appId: '',
            appInfoId: 0,
            customId: 'NewStudyTest',
            enrolledCount: 41,
            enrollmentPercentage: 38,
            id: 1,
            invitedCount: 0,
            name: 'New Study Test',
            sites: [],
            studyPermission: 0,
            totalSitesCount: 16,
            type: 'OPEN',
          },
          {
            appId: '',
            appInfoId: 0,
            customId: 'OpenStudy',
            enrolledCount: 5,
            enrollmentPercentage: 0,
            id: 12,
            invitedCount: 9,
            name: 'Open Study 02',
            sites: [],
            studyPermission: 1,
            totalSitesCount: 5,
            type: 'OPEN',
          },
          {
            appId: '',
            appInfoId: 0,
            customId: 'ClosedStudy',
            enrolledCount: 54,
            enrollmentPercentage: 17,
            id: 14,
            invitedCount: 0,
            name: 'Closed Study',
            sites: [],
            studyPermission: 2,
            totalSitesCount: 6,
            type: 'CLOSE',
          },
        ]);
        studyServiceSpy.getStudies.and.returnValue(expectedStudyList);
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should NOT have studies before ngOnInit', () => {
    expect(component.studies.length).toBe(
      0,
      'should not have studies before ngOnInit',
    );
  });
  it('should NOT have studies immediately after ngOnInit', () => {
    expect(component.studies.length).toBe(
      0,
      'should not have studies until service promise resolves',
    );
  });
  describe('after get studies', () => {
    beforeEach(async(() => {
      fixture.detectChanges();
      void fixture.whenStable().then(() => {
        fixture.detectChanges();
      });
    }));
    it('should not have search box ', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.querySelector('.search-icon')?.classList.length).toBe(
        undefined,
        'should not have search box',
      );
    });
    it('should get the studies List via refresh function', fakeAsync(() => {
      component.study$.subscribe((studies) => {
        expect(studies.length).toEqual(3);
      });
    }));
    it('should DISPLAY All Studies', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      fixture.detectChanges();
      expect(compiled.querySelectorAll('.studies_row').length).toBe(
        3,
        'should display all studies list',
      );
    });
  });
});
