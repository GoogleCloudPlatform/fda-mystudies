import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginCallbackComponent} from './login-callback.component';

describe('LoginCallbackComponent', () => {
  let component: LoginCallbackComponent;
  let fixture: ComponentFixture<LoginCallbackComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginCallbackComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginCallbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
