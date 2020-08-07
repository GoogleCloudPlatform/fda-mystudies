import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SetUpAccountComponent} from './set-up-account.component';

describe('SetUpAccountComponent', () => {
  let component: SetUpAccountComponent;
  let fixture: ComponentFixture<SetUpAccountComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [SetUpAccountComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SetUpAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
