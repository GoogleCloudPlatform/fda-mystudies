import {
  async,
  TestBed,
  tick,
  ComponentFixture,
  fakeAsync,
} from '@angular/core/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LocationListComponent} from './location-list.component';
import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';
import {LocationModule} from '../../location/location.module';
import {ToastrModule} from 'ngx-toastr';
import {EntityService} from '../../../service/entity.service';
import {of} from 'rxjs';
import {LocationService} from '../shared/location.service';
describe('LocationsListComponent', () => {
  let component: LocationListComponent;
  let fixture: ComponentFixture<LocationListComponent>;
  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [LocationListComponent],
      imports: [
        RouterTestingModule,
        LocationModule,
        BrowserAnimationsModule,
        HttpClientModule,
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [EntityService, LocationService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationListComponent);
    component = fixture.componentInstance;
  });
  it('should create', () => {
    const apps = fixture.debugElement
      .componentInstance as LocationListComponent;
    expect(apps).toBeTruthy();
  });

  it('should NOT have locations before ngOnInit', () => {
    const apps = fixture.debugElement
      .componentInstance as LocationListComponent;
    expect(apps.locations.length).toBe(
      0,
      'should not have locations before ngOnInit',
    );
  });
  it('should DISPLAY Locations', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.subheader__title')?.classList.length).toBe(
      1,
      'should display all locations list',
    );
  });

  it('should not have search box ', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.search-icon')?.classList.length).toBe(
      undefined,
      'should not have search box',
    );
  });

  it('should get the Location List via refresh function', fakeAsync(() => {
    const expectedList = [
      {
        id: 2,
        customId: 'customid3',
        name: 'name -1-updated0',
        description: 'location-descp-updatedj',
        status: '1',
        studiesCount: 0,
        studies: [],
      },
      {
        id: 3,
        customId: 'customid32',
        name: 'name -1 - updated000',
        description: 'location-descp-updated',
        status: '0',
        studiesCount: 0,
        studies: [],
      },
    ];
    const locationServiceSpy = jasmine.createSpyObj<LocationService>(
      'LocationService',
      ['getLocations'],
    );
    locationServiceSpy.getLocations.and.returnValue(of(expectedList));
    spyOn(component, 'getLocation');
    component.getLocation();
    tick();
    fixture.detectChanges();
    expect(component.locations).toEqual(component.locations);
  }));
});
