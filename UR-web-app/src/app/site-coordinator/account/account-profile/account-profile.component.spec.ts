import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountProfileComponent} from './account-profile.component';

describe('UserProfileComponent', () => {
  let component: AccountProfileComponent;
  let fixture: ComponentFixture<AccountProfileComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [AccountProfileComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
