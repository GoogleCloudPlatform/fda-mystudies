import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {SiteCoordinatorComponent} from './sitecoordinator.component';
import {UserService} from '../service/user.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
describe('SitecoordinatorComponent', () => {
  let component: SiteCoordinatorComponent;
  let fixture: ComponentFixture<SiteCoordinatorComponent>;
  let header: DebugElement;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [SiteCoordinatorComponent],
      providers: [UserService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SiteCoordinatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    header = fixture.debugElement.query(By.css('#navbarCollapse'));
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(SiteCoordinatorComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should display the contents of header if displayHeaderOnResetpassword is true', () => {
    const headers = header.nativeElement as HTMLInputElement;
    component.displayHeaderOnResetpassword = true;
    fixture.detectChanges();
    expect(headers).toBeTruthy();
  });
});
