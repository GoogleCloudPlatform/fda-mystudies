import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
} from '@angular/core/testing';

import {StudyDetailsComponent} from './study-details.component';
import {ModalModule, BsModalRef} from 'ngx-bootstrap/modal';
import {RouterTestingModule} from '@angular/router/testing';
import {StudiesModule} from '../studies.module';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastrModule} from 'ngx-toastr';
import {StudyDetailsService} from '../shared/study-details.service';
import {StudyDetails} from '../shared/study-details';
import {expectedStudiesDetails} from 'src/app/entity/mock-studies-data';
import {of} from 'rxjs';
import {EntityService} from 'src/app/service/entity.service';

describe('StudyDetailsComponent', () => {
  let component: StudyDetailsComponent;
  let fixture: ComponentFixture<StudyDetailsComponent>;

  beforeEach(async(async () => {
    const studyServiceSpy = jasmine.createSpyObj<StudyDetailsService>(
      'StudyDetailsService',
      {getStudyDetails: of(expectedStudiesDetails)},
    );
    await TestBed.configureTestingModule({
      declarations: [StudyDetailsComponent],
      imports: [
        ModalModule.forRoot(),
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
        BsModalRef,
        {provide: StudyDetailsService, useValue: studyServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(StudyDetailsComponent);
        component = fixture.componentInstance;
        studyServiceSpy.getStudyDetails.and.returnValues(
          of(expectedStudiesDetails),
        );
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should NOT have study`s participant list before ngOnInit', () => {
    component.studyDetail$.pipe().subscribe((studyDetail: StudyDetails) => {
      expect(
        studyDetail.participantRegistryDetail.registryParticipants.length,
      ).toBe(0, 'should NOT have study`s participant list before ngOnInit');
    });
  });

  describe('after get studies', () => {
    beforeEach(async(() => {
      fixture.detectChanges();
      void fixture.whenStable().then(() => {
        fixture.detectChanges();
      });
    }));

    it('should get the list of study participants via refresh function', fakeAsync(() => {
      component.studyDetail$.subscribe((studyDetail) => {
        expect(
          studyDetail.participantRegistryDetail.registryParticipants.length,
        ).toEqual(
          expectedStudiesDetails.participantRegistryDetail.registryParticipants
            .length,
        );
        expect(studyDetail.participantRegistryDetail.appId).toEqual(
          expectedStudiesDetails.participantRegistryDetail.appId,
        );
        expect(studyDetail.participantRegistryDetail.studyId).toEqual(
          expectedStudiesDetails.participantRegistryDetail.studyId,
        );
      });
    }));
  });
});
