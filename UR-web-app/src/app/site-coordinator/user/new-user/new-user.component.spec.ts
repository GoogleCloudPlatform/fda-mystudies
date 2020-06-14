import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AddNewUserComponent} from './new-user.component';

describe('AddNewUserComponent', () => {
  let component: AddNewUserComponent;
  let fixture: ComponentFixture<AddNewUserComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddNewUserComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddNewUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
