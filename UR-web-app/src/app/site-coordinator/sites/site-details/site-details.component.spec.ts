import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {SiteDetailsComponent} from './site-details.component';
import {ModalModule, BsModalRef} from 'ngx-bootstrap/modal';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastrModule} from 'ngx-toastr';
import {SiteDetailsService} from '../shared/site-details.service';
import {of} from 'rxjs';
import {SiteCoordinatorModule} from '../../../site-coordinator/site-coordinator.module';
import {EntityService} from 'src/app/service/entity.service';
import {expectedSiteParticipantDetails} from 'src/app/entity/mock-sitedetail-data';
import {By} from '@angular/platform-browser';
import {NgxDataTableModule} from 'angular-9-datatable';
import {
  expectedToggleResponse,
  expectedSendInviteResponse,
  expectedDecommissionResponse,
} from 'src/app/entity/mock-participant-data';
import {SitesModule} from '../sites.module';

describe('SiteDetailsComponent', () => {
  let component: SiteDetailsComponent;
  let fixture: ComponentFixture<SiteDetailsComponent>;

  beforeEach(async(async () => {
    const siteDetailServiceSpy = jasmine.createSpyObj<SiteDetailsService>(
      'SiteDetailsService',
      {
        get: of(expectedSiteParticipantDetails),
        toggleInvitation: of(expectedToggleResponse),
        sendInvitation: of(expectedSendInviteResponse),
        siteDecommission: of(expectedDecommissionResponse),
      },
    );
    await TestBed.configureTestingModule({
      declarations: [SiteDetailsComponent],
      imports: [
        ModalModule.forRoot(),
        RouterTestingModule,
        SitesModule,
        BrowserAnimationsModule,
        HttpClientModule,
        SiteCoordinatorModule,
        NgxDataTableModule,
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [
        EntityService,
        BsModalRef,
        {provide: SiteDetailsService, useValue: siteDetailServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(SiteDetailsComponent);
        component = fixture.componentInstance;
      });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SiteDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('after get site participants', () => {
    beforeEach(async(() => {
      fixture.detectChanges();
      void fixture.whenStable().then(() => {
        fixture.detectChanges();
      });
    }));

    it('should change tab on click on tab', fakeAsync(async () => {
      fixture.detectChanges();
      const changetab = fixture.debugElement.query(By.css('.classNew'))
        .nativeElement as HTMLInputElement;
      component.changeTab('new');
      const changeTabSpy = spyOn(component, 'changeTab');
      fixture.detectChanges();
      tick();
      changetab.click();
      fixture.detectChanges();
      await fixture.whenStable();
      expect(component.activeTab).toEqual('new');
      expect(changeTabSpy).toHaveBeenCalledTimes(1);
    }));
    it('it should open default tab when site details is loaded', () => {
      expect(component.activeTab).toEqual('all');
    });

    it('should decommission the site when  button is clicked', fakeAsync(async () => {
      fixture.autoDetectChanges();
      component.siteId = '2';
      component.decommissionSite();
      const decommisionButton = fixture.debugElement.query(
        By.css('[name="buttonDecommision"]'),
      ).nativeElement as HTMLInputElement;
      expect(component.siteId).toBeDefined();
      const changeTabSpy = spyOn(component, 'decommissionSite');
      decommisionButton.click();
      tick(10000);
      await fixture.whenStable();
      expect(changeTabSpy).toHaveBeenCalledTimes(1);
    }));

    it('should enable/disable the invitation when toggle button is clicked', fakeAsync(async () => {
      component.changeTab('invited');
      fixture.detectChanges();
      expect(component.activeTab).toEqual('invited');
      fixture.detectChanges();
      const toggleInviteButton = fixture.debugElement.query(
        By.css('[name="toggleInvite"]'),
      ).nativeElement as HTMLInputElement;
      expect(toggleInviteButton).toBeTruthy();
      component.toggleInvitation();
      fixture.detectChanges();
      component.arrayOfuserId = ['1', '2'];
      const toggleChangeSpy = spyOn(component, 'toggleInvitation');
      expect(component.arrayOfuserId.length > 0).toBeTruthy();
      expect(component.arrayOfuserId.length < 11).toBeTruthy();
      fixture.detectChanges();
      toggleInviteButton.click();
      tick(10000);
      await fixture.whenStable();
      expect(toggleChangeSpy).toHaveBeenCalledTimes(1);
    }));

    it('should send the invitation when send button is clicked', fakeAsync(async () => {
      component.changeTab('new');
      fixture.detectChanges();
      expect(component.activeTab).toEqual('new');
      fixture.detectChanges();
      const sendInviteButton = fixture.debugElement.query(
        By.css('[name="sendInvite"]'),
      ).nativeElement as HTMLInputElement;
      expect(sendInviteButton).toBeTruthy();
      component.arrayOfuserId = ['1', '2'];
      expect(component.arrayOfuserId.length > 0).toBeTruthy();
      expect(component.arrayOfuserId.length < 11).toBeTruthy();
      component.sendInvitation();
      const sendInviteSpy = spyOn(component, 'sendInvitation');
      sendInviteButton.click();
      fixture.detectChanges();
      tick(10000);
      expect(component.arrayOfuserId);
      await fixture.whenStable();
      expect(sendInviteSpy).toHaveBeenCalledTimes(1);
    }));

    it('should slide down up on click participant drop down', fakeAsync(async () => {
      spyOn(component, 'toggleParticipant').and.callThrough();
      component.toggleParticipant();
      fixture.detectChanges();
      tick(1000);
      await fixture.whenStable();
      // eslint-disable-next-line @typescript-eslint/unbound-method
      expect(component.toggleParticipant).toHaveBeenCalled();
    }));
  });
});
