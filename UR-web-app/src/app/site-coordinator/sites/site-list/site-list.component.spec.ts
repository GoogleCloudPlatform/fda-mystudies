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
import {SitesService} from '../shared/sites.service';
import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';
import {ToastrModule} from 'ngx-toastr';
import {EntityService} from '../../../service/entity.service';
import {of} from 'rxjs';
import {BsModalService, BsModalRef, ModalModule} from 'ngx-bootstrap/modal';
import {
  expectedStudyList,
  expectedStudyId,
  expectedLocations,
  expectedSiteResponse,
} from '../../../entity/mockData';
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
      ['getStudiesWithSites'],
    );
    const locationServiceSpy = jasmine.createSpyObj<LocationService>(
      'LocationService',
      ['getLocationsForSiteCreation'],
    );
    const sitesServiceSpy = jasmine.createSpyObj<SitesService>('SitesService', [
      'addSite',
    ]);
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

        studyServiceSpy.getStudiesWithSites.and.returnValue(
          of(expectedStudyList),
        );
        locationServiceSpy.getLocationsForSiteCreation.and.returnValue(
          of(expectedLocations),
        );
        sitesServiceSpy.addSite.and.returnValue(of(expectedSiteResponse));
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

  it('should check the input value', () => {
    component.study$.subscribe((studies) => {
      expect(studies[0].sites[0].name).toEqual('Location 1');
      expect(studies[1].sites[0].name).toEqual('Location 2');
      expect(studies[1].sites[1].name).toEqual('Location 3');
    });
  });

  it('should display sites with progress', async () => {
    const compiled = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();
    await fixture.whenStable();
    expect(compiled.querySelectorAll('.studies_row').length).toBe(
      3,
      'should display all study list',
    );
    const sitesLists = compiled.querySelectorAll('.sites_row');
    const sitesListPCT = compiled.querySelectorAll('.enrolled');
    expect(sitesLists[0].innerHTML).toEqual('Location 1');
    expect(sitesListPCT[0].innerHTML).toEqual('7 / 14');
  });

  it('should display the modal when Add site is clicked', fakeAsync(() => {
    const clickAddButton = fixture.debugElement.query(
      By.css('button[name="add"]'),
    );
    const clickSpyobj = spyOn(component, 'openAddSiteModal');
    clickAddButton.triggerEventHandler('click', null);
    tick();
    fixture.detectChanges();
    expect(clickSpyobj).toBeTruthy();
    flush();
  }));

  it('should call get location list ', fakeAsync(() => {
    const spyobjs = spyOn(component, 'getLocation');
    component.getLocation(expectedStudyId);
    fixture.detectChanges();
    expect(spyobjs.calls.count()).toBe(1);
  }));
});
