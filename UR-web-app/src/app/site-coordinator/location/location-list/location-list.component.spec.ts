import {async, TestBed, tick, fakeAsync} from '@angular/core/testing';

import {LocationListComponent} from './location-list.component';
import {By} from '@angular/platform-browser';
import {EntityService} from 'src/app/service/entity.service';
import {HttpClientModule} from '@angular/common/http';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {RouterTestingModule} from '@angular/router/testing';
import {LocationModule} from '../../location/location.module';
import {ToastrModule} from 'ngx-toastr';
describe('LocationsListComponent', () => {
  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [LocationListComponent],
      imports: [
        RouterTestingModule,
        LocationModule,
        SiteCoordinatorModule,
        HttpClientModule,
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [EntityService],
    }).compileComponents();
  }));

  it('should create', () => {
    const fixture = TestBed.createComponent(LocationListComponent);
    const apps = fixture.debugElement
      .componentInstance as LocationListComponent;
    expect(apps).toBeTruthy();
  });

  it('should NOT have locations before ngOnInit', () => {
    const fixture = TestBed.createComponent(LocationListComponent);
    const component = fixture.debugElement
      .componentInstance as LocationListComponent;
    expect(component.locations.length).toBe(
      0,
      'should not have locations before ngOnInit',
    );
  });
  it('should DISPLAY Locations', () => {
    const fixture = TestBed.createComponent(LocationListComponent);
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.subheader__title')?.classList.length).toBe(
      1,
      'should display all locations list',
    );
  });

  it('should not have search box ', () => {
    const fixture = TestBed.createComponent(LocationListComponent);
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.search-icon')?.classList.length).toBe(
      undefined,
      'should not have search box',
    );
  });
  it('should call onClick method', () => {
    const fixture = TestBed.createComponent(LocationListComponent);
    const component = fixture.debugElement
      .componentInstance as LocationListComponent;
    fixture.detectChanges();
    const onClickMock = spyOn(component, 'addLocation');
    fixture.debugElement
      .query(By.css('button'))
      .triggerEventHandler('click', null);
    expect(onClickMock).toHaveBeenCalled();
  });

  it('should auto click on New AddLocation Button', async(() => {
    const fixture = TestBed.createComponent(LocationListComponent);
    const component = fixture.componentInstance;
    spyOn(component, 'addLocation');
    fixture.detectChanges();
    const button = fixture.nativeElement as HTMLElement;
    button.querySelector('button')?.click();

    void fixture.whenStable().then(() => {
      expect(component.addLocation).toHaveBeenCalled();
    });
  }));
  it('should get the user List via refresh function', fakeAsync(() => {
    const fixture = TestBed.createComponent(LocationListComponent);
    const component = fixture.componentInstance;
    spyOn(component, 'getLocation');
    component.getLocation();
    tick();
    fixture.detectChanges();
    expect(component.locations.length).toBe(
      0,
      'Location list after function call',
    );
  }));
});
