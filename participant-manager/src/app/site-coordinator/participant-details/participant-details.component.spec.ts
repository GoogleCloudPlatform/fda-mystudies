import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {ModalModule, BsModalRef} from 'ngx-bootstrap/modal';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastrModule} from 'ngx-toastr';
import {ParticipantDetailsService} from './participant-details.service';
import {of} from 'rxjs';
import {SiteCoordinatorModule} from '../site-coordinator.module';
import {EntityService} from 'src/app/service/entity.service';
import {ParticipantDetailsComponent} from './participant-details.component';
import {
  expectedParticipantDetails,
  expectedToggleResponse,
  expectedSendInviteResponse,
} from 'src/app/entity/mock-participant-data';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';

describe('ParticipantDetailsComponent', () => {
  let component: ParticipantDetailsComponent;
  let fixture: ComponentFixture<ParticipantDetailsComponent>;
  let sendInviteButton: DebugElement;
  let toggleInviteButton: DebugElement;
  beforeEach(async(async () => {
    const participantDetailsSpy = jasmine.createSpyObj<ParticipantDetailsService>(
      'ParticipantDetailsService',
      {
        get: of(expectedParticipantDetails),
        toggleInvitation: of(expectedToggleResponse),
        sendInvitation: of(expectedSendInviteResponse),
      },
    );

    await TestBed.configureTestingModule({
      declarations: [ParticipantDetailsComponent],
      imports: [
        ModalModule.forRoot(),
        RouterTestingModule,
        BrowserAnimationsModule,
        SiteCoordinatorModule,
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
        {provide: ParticipantDetailsService, useValue: participantDetailsSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(ParticipantDetailsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        sendInviteButton = fixture.debugElement.query(
          By.css('[name="sendInvite"]'),
        );
        toggleInviteButton = fixture.debugElement.query(
          By.css('[name="toggleInvite"]'),
        );
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call get participant details on ngOnInit', fakeAsync(() => {
    const spyObjs = spyOn(component, 'getParticipant');
    component.getParticipant();
    fixture.detectChanges();
    expect(spyObjs).toHaveBeenCalledTimes(1);
  }));

  it('should check participant details are not empty', () => {
    component.participant$.subscribe((participant) => {
      expect(participant.participantDetails).toEqual(
        expectedParticipantDetails.participantDetails,
      );
    });
  });

  it('should check participant `email` and `id` is mandatory', () => {
    component.participant$.subscribe((participant) => {
      expect(participant.participantDetails.email).toEqual(
        expectedParticipantDetails.participantDetails.email,
      );
    });
  });

  it('should enable/disable the invitation when toggle button is clicked', fakeAsync(async () => {
    const toggleChangeSpy = spyOn(component, 'toggleInvitation');
    const toggleButton = toggleInviteButton.nativeElement as HTMLInputElement;
    fixture.detectChanges();
    tick();
    toggleButton.click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(toggleChangeSpy).toHaveBeenCalledTimes(1);
  }));

  it('should send the invitation when send button is clicked', fakeAsync(async () => {
    const sendInviteSpy = spyOn(component, 'sendInvitation');
    const sendButton = sendInviteButton.nativeElement as HTMLInputElement;
    fixture.detectChanges();
    tick();
    sendButton.click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(sendInviteSpy).toHaveBeenCalledTimes(1);
  }));
});
