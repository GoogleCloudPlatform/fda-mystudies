import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AuthService} from 'src/app/service/auth.service';
import {HttpClientModule} from '@angular/common/http';
import {EntityService} from 'src/app/service/entity.service';
import {RouterTestingModule} from '@angular/router/testing';
import {LoginComponent} from './login.component';
import {LoginCallbackComponent} from '../login-callback/login-callback.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [AuthService, EntityService],
      imports: [
        HttpClientModule,
        RouterTestingModule.withRoutes([
          {
            path: 'callback',
            component: LoginCallbackComponent,
          },
        ]),
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
