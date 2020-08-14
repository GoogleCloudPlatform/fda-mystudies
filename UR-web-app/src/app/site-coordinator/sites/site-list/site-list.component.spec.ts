import {
  async,
  TestBed,
  ComponentFixture,
  fakeAsync,
  tick,
  flush,
} from '@angular/core/testing';
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
import {expectedStudyList} from '../../../entity/mock-studies-data';
import {StudiesService} from '../../studies/shared/studies.service';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {LocationService} from '../../location/shared/location.service';
import {By} from '@angular/platform-browser';

describe('SiteListComponent', () => {
  let component: SiteListComponent;
  let fixture: ComponentFixture<SiteListComponent>;
  beforeEach(async(async () => {
    const studyServiceSpy = jasmine.createSpyObj<StudiesService>(
      'studiesService',
      {getStudiesWithSites: of(expectedStudyList)},
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
      expect(studies.length).toEqual(3);
      expect(studies[0].sites.length).toEqual(1);
      expect(studies[1].sites.length).toEqual(2);
    });
  });

  it('should expect same location details', () => {
    component.study$.subscribe((studies) => {
      expect(studies[0].sites[0].name).toEqual('Location 1');
      expect(studies[1].sites[0].name).toEqual('Location 2');
      expect(studies[1].sites[1].name).toEqual('Location 3');
    });
  });

  it('should display sites with progress bar', async () => {
    const compiled = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();
    await fixture.whenStable();
    expect(compiled.querySelectorAll('.studies_row').length).toBe(
      3,
      'should display all study list',
    );
    const sitesLists = compiled.querySelectorAll('.sites_row');
    const sitesListPCT = compiled.querySelectorAll('.enrolled');
    expect(sitesLists[0].textContent).toContain('Location 1');
    expect(sitesListPCT[0].textContent).toContain('7 / 14');
  });

  it('should display the modal when Add site button is clicked', fakeAsync(async () => {
    const clickAddButton = fixture.debugElement.query(
      By.css('button[name="add"]'),
    );
    const clickSpyobj = spyOn(component, 'openAddSiteModal');
    clickAddButton.triggerEventHandler('click', null);
    tick();
    await fixture.whenStable();
    fixture.detectChanges();
    expect(clickSpyobj).toBeTruthy();
    flush();
  }));
});
