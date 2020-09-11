import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {AuthService} from 'src/app/service/auth.service';
import {HttpClientModule} from '@angular/common/http';
import {EntityService} from 'src/app/service/entity.service';
import {LoginCallbackComponent} from './login-callback.component';
import {LoginComponent} from '../login/login.component';

describe('LoginCallbackComponent', () => {
  let component: LoginCallbackComponent;
  let fixture: ComponentFixture<LoginCallbackComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginCallbackComponent],
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: 'login',
            component: LoginComponent,
          },
        ]),
        HttpClientModule,
      ],
      providers: [AuthService, EntityService],
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
