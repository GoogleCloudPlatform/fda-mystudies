import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {ImportEmailListComponent} from './import-email-list.component';
import {SiteDetailsService} from '../shared/site-details.service';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastrModule} from 'ngx-toastr';
import {of} from 'rxjs';
import {EntityService} from 'src/app/service/entity.service';
import * as expectedResult from 'src/app/entity/mock-sitedetail-data';
import {SitesModule} from '../sites.module';

describe('ImportEmailListComponent', () => {
  let component: ImportEmailListComponent;
  let fixture: ComponentFixture<ImportEmailListComponent>;
  beforeEach(async(async () => {
    const siteDetailsServiceSpy = jasmine.createSpyObj<SiteDetailsService>(
      'SiteDetailsService',
      {
        importParticipants: of(
          expectedResult.expectedImportedEmailListResponse,
        ),
      },
    );
    await TestBed.configureTestingModule({
      declarations: [ImportEmailListComponent],
      imports: [
        SitesModule,
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
        {provide: SiteDetailsService, useValue: siteDetailsServiceSpy},
      ],
    }).compileComponents();
  }));
  beforeEach(() => {
    fixture = TestBed.createComponent(ImportEmailListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should update the profile when  button is submitted', fakeAsync(async () => {
    spyOn(component, 'importParticipants').and.callThrough();
    component.importParticipants();
    fixture.detectChanges();
    tick(10000);
    await fixture.whenStable();
    // eslint-disable-next-line @typescript-eslint/unbound-method
    expect(component.importParticipants).toHaveBeenCalled();
  }));
  it('should hide component onclick cancel button', fakeAsync(async () => {
    spyOn(component, 'onCancel').and.callThrough();
    component.onCancel();
    fixture.detectChanges();
    tick(100);
    await fixture.whenStable();
    // eslint-disable-next-line @typescript-eslint/unbound-method
    expect(component.onCancel).toHaveBeenCalled();
  }));
});
