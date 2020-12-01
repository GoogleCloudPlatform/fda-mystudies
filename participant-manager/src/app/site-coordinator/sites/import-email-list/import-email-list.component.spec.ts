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
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
describe('ImportEmailListComponent', () => {
  let component: ImportEmailListComponent;
  let fixture: ComponentFixture<ImportEmailListComponent>;
  let importParticipantButton: DebugElement;
  let cancelButtonName: DebugElement;

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

    cancelButtonName = fixture.debugElement.query(
      By.css('[name="buttonCancel"]'),
    );
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should hide component onclick cancel button', fakeAsync(async () => {
    const cancelSpy = spyOn(component, 'cancelled');
    const cancelButton = cancelButtonName.nativeElement as HTMLInputElement;
    fixture.detectChanges();
    cancelButton.click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(cancelSpy).toHaveBeenCalledTimes(1);
  }));
});
