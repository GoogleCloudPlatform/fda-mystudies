import {
  async,
  TestBed,
  ComponentFixture,
  fakeAsync,
} from '@angular/core/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {StudyListComponent} from './study-list.component';

import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';
import {ToastrModule} from 'ngx-toastr';
import {EntityService} from '../../../service/entity.service';
import {of, Observable} from 'rxjs';
import {StudiesModule} from '../studies.module';
import {StudiesService} from '../shared/studies.service';
import {DashboardModel} from '../shared/dashboard.model';

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
        RouterTestingModule,
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
        {provide: StudiesService, useValue: studyServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(StudyListComponent);
        component = fixture.componentInstance;
        const expectedStudyList: Observable<DashboardModel[]> = of([
          {
            appId: 'dsdssd',
            appInfoId: 2,
            customId: 'dsasd',
            enrolledCount: 3,
            enrollmentPercentage: 25,
            id: 12,
            invitedCount: 2,
            name: 'dsadasd',
            sites: [],
            studyPermission: 2,
            totalSitesCount: 2,
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
        expect(studies.length).toEqual(1);
      });
    }));
    it('should DISPLAY Locations', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      fixture.detectChanges();
      expect(compiled.querySelectorAll('.studies_row').length).toBe(
        1,
        'should display all studies list',
      );
    });
  });
});
