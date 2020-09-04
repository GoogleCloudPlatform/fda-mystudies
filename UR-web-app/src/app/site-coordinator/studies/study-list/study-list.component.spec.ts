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
import {BsModalService, BsModalRef, ModalModule} from 'ngx-bootstrap/modal';
import {StudiesModule} from '../studies.module';
import {StudiesService} from '../shared/studies.service';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {expectedStudyList} from 'src/app/entity/mock-studies-data';
import {of} from 'rxjs';

describe('StudyListComponent', () => {
  let component: StudyListComponent;
  let fixture: ComponentFixture<StudyListComponent>;

  beforeEach(async(async () => {
    const studyServiceSpy = jasmine.createSpyObj<StudiesService>(
      'StudiesService',
      {getStudies: of(expectedStudyList)},
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
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should NOT have studies before ngOnInit', () => {
    component.studyList$.subscribe((studies) => {
      expect(studies.studies.length).toBe(
        0,
        'should not have studies before ngOnInit',
      );
    });
  });

  describe('after get studies', () => {
    beforeEach(async(() => {
      fixture.detectChanges();
      void fixture.whenStable().then(() => {
        fixture.detectChanges();
      });
    }));

    it('should get the studies List via refresh function', fakeAsync(() => {
      component.studyList$.subscribe((studies) => {
        expect(studies.studies.length).toEqual(4);
      });
    }));
    it('should display all studies', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      fixture.detectChanges();
      expect(compiled.querySelectorAll('.studies_row').length).toBe(
        4,
        'should display all studies list',
      );
    });
  });
});
