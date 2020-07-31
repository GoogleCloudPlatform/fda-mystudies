import {
  async,
  TestBed,
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
import {of, Observable} from 'rxjs';
import {LocationService} from '../shared/location.service';
import {Location} from '../shared/location.model';

describe('LocationsListComponent', () => {
  let component: LocationListComponent;
  let fixture: ComponentFixture<LocationListComponent>;

  beforeEach(async(async () => {
    const locationServiceSpy = jasmine.createSpyObj<LocationService>(
      'LocationService',
      ['getLocations'],
    );
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
      providers: [
        EntityService,
        {provide: LocationService, useValue: locationServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(LocationListComponent);
        component = fixture.componentInstance;
        const expectedList: Observable<Location[]> = of([
          {
            id: 2,
            customId: 'customid3',
            name: 'name -1-updated0',
            description: 'location-descp-updatedj',
            status: '1',
            studiesCount: 0,
            message: '',
            code: '',
          },
          {
            id: 3,
            customId: 'customid32',
            name: 'name -1 - updated000',
            description: 'location-descp-updated',
            status: '0',
            studiesCount: 0,
            message: '',
            code: '',
          },
        ]);
        locationServiceSpy.getLocations.and.returnValue(expectedList);
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should NOT have locations before ngOnInit', () => {
    component.location$.pipe().subscribe((location: Location[]) => {
      expect(location.length).toBe(
        0,
        'should not have locations before ngOnInit',
      );
    });
  });
  it('should NOT have locations immediately after ngOnInit', () => {
    component.location$.pipe().subscribe((location: Location[]) => {
      expect(location.length).toBe(
        0,
        'should not have locations until service promise resolves',
      );
    });
  });
  describe('after get locations', () => {
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

    it('should get the Location List via refresh function', fakeAsync(() => {
      component.location$.subscribe((locations) => {
        expect(locations.length).toEqual(2);
      });
    }));

    it('should DISPLAY Locations', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      fixture.detectChanges();
      expect(compiled.querySelectorAll('.location_row').length).toBe(
        2,
        'should display all locations list',
      );
    });
  });
});
