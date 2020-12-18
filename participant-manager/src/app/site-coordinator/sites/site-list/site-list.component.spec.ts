import {async, TestBed, ComponentFixture} from '@angular/core/testing';
import {
  BrowserAnimationsModule,
  NoopAnimationsModule,
} from '@angular/platform-browser/animations';
import {SiteListComponent} from './site-list.component';
import {SitesModule} from '../sites.module';
import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';
import {ToastrModule} from 'ngx-toastr';
import {EntityService} from '../../../service/entity.service';
import {of} from 'rxjs';
import {BsModalService, BsModalRef, ModalModule} from 'ngx-bootstrap/modal';
import {expectedSitesList} from '../../../entity/mock-studies-data';
import {StudiesService} from '../../studies/shared/studies.service';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {LocationService} from '../../location/shared/location.service';

describe('SiteListComponent', () => {
  let component: SiteListComponent;
  let fixture: ComponentFixture<SiteListComponent>;
  beforeEach(async(async () => {
    const studyServiceSpy = jasmine.createSpyObj<StudiesService>(
      'studiesService',
      {getStudiesWithSites: of(expectedSitesList)},
    );
    await TestBed.configureTestingModule({
      declarations: [SiteListComponent],
      imports: [
        SiteCoordinatorModule,
        BrowserAnimationsModule,
        SitesModule,
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
        LocationService,
        BsModalService,
        BsModalRef,
        {provide: StudiesService, useValue: studyServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(SiteListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  beforeEach(async(async () => {
    fixture.detectChanges();
    await fixture.whenStable();
  }));

  it('should get the sites list via refresh function', () => {
    component.study$.subscribe((studies) => {
      expect(studies.studies.length).toEqual(2);
    });
  });

  it('should expect same location details', () => {
    component.study$.subscribe((studies) => {
      expect(studies.studies[0].sites).toBeTruthy();
      expect(studies.studies[0].sites[0].name).toEqual('Location1');
    });
  });

  it('should display sites with progress bar', async () => {
    const compiled = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();
    await fixture.whenStable();
    expect(compiled.querySelectorAll('.studies_row').length).toBe(
      2,
      'should display all study list',
    );
  });
});
