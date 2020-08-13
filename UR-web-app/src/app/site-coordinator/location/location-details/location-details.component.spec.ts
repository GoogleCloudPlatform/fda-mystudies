import {
  async,
  TestBed,
  ComponentFixture,
  fakeAsync,
} from '@angular/core/testing';

import {LocationDetailsComponent} from './location-details.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';
import {LocationModule} from '../../location/location.module';
import {ToastrModule} from 'ngx-toastr';
import {EntityService} from '../../../service/entity.service';
import {of} from 'rxjs';
import {LocationService} from '../shared/location.service';
import {ModalModule, BsModalRef} from 'ngx-bootstrap/modal';
import {By} from '@angular/platform-browser';
import {
  expectedLocation,
  expectedResponse,
} from 'src/app/entity/mock-location-data';

describe('LocationDetailsComponent', () => {
  let component: LocationDetailsComponent;
  let fixture: ComponentFixture<LocationDetailsComponent>;
  beforeEach(async(async () => {
    const locationServiceSpy = jasmine.createSpyObj<LocationService>(
      'LocationService',
      ['get', 'update'],
    );

    await TestBed.configureTestingModule({
      declarations: [LocationDetailsComponent],
      imports: [
        ModalModule.forRoot(),
        RouterTestingModule.withRoutes([
          {
            path: 'coordinator/locations',
            component: LocationDetailsComponent,
          },
        ]),
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
        BsModalRef,
        {provide: LocationService, useValue: locationServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(LocationDetailsComponent);
        component = fixture.componentInstance;
        locationServiceSpy.get.and.returnValue(of(expectedLocation));
        locationServiceSpy.update.and.returnValue(of(expectedResponse));
        fixture.detectChanges();
        fixture.destroy();
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call get Location Details in ngOninit', fakeAsync(() => {
    const spyobjs = spyOn(component, 'getLocationDetails');
    component.getLocationDetails();
    fixture.detectChanges();
    expect(spyobjs).toHaveBeenCalledTimes(1);
  }));

  it('should check input value is filled after data', () => {
    fixture.detectChanges();
    expect(component.location.customId).toEqual('customid3');
    expect(component.location.description).toEqual('location-descp-updated');
    expect(component.location.name).toEqual('name -1-updated0');
  });

  it('should not have studies when count is zero', () => {
    fixture.detectChanges();
    const elem = fixture.debugElement.query(
      By.css('[data-test="studiesCount"]'),
    );
    expect(elem).toBeFalsy();
  });
});
