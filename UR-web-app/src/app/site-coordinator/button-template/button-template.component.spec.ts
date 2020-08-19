import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ButtonTemplateComponent} from './button-template.component';

describe('ButtonTemplateComponent', () => {
  let component: ButtonTemplateComponent;
  let fixture: ComponentFixture<ButtonTemplateComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [ButtonTemplateComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
