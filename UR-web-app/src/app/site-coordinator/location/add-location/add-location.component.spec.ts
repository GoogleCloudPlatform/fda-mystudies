import {async, ComponentFixture, TestBed, inject} from '@angular/core/testing';

import {AddLocationComponent} from './add-location.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Component} from '@angular/core';

describe('AddLocationComponent', () => {
  let component: AddLocationComponent;
  let fixture: ComponentFixture<AddLocationComponent>;
  let addLocations: AddLocationComponent;
  let expectedloationCustomId = '1';
  let expectedloationName = 'LocationName1';
  let expectedlocationDescription = 'This is a test case Description';
  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddLocationComponent],
      imports: [FormsModule, ReactiveFormsModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddLocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  beforeEach(inject(
    [AddLocationComponent],
    (addLocationForm: AddLocationComponent) => {
      addLocations = addLocationForm;
    },
  ));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should send data on submit', () => {
    addLocations.submitted.subscribe(
      ({
        loationCustomId: number,
        loationName: string,
        locationDescription: string,
      }) => {
        expect(loationCustomId).toEqual(expectedloationCustomId);
        expect(loationName).toEqual(expectedloationName);
        expect(locationDescription).toEqual(expectedlocationDescription);
      },
    );

    addLocations.onSubmit({
      loationCustomId: expectedloationCustomId,
      loationName: expectedloationName,
      locationDescription: expectedlocationDescription,
    });
  });
});

// describe('Shallow', () => {
//   let component: AddLocationComponent;
//   let fixture: ComponentFixture<AddLocationComponent>;
//   let element;

//   let addLocations: AddLocationComponent;
//   beforeEach(async(() => {
//     await TestBed.configureTestingModule({
//       declarations: [AddLocationComponent],
//       imports: [FormsModule, ReactiveFormsModule],
//     }).compileComponents();
//   }));

//   it('should send credentials on submit', () => {
//     fixture = TestBed.createComponent(AddLocationComponent);
//     component = fixture.componentInstance;
//     const element = fixture.nativeElement;

//     fixture.detectChanges();

//     element.querySelector('#loationCustomId').value = expectedloationCustomId;
//     element.querySelector('#loationCustomId').dispatchEvent(new Event('input'));
//     element.querySelector('#loationName').value = expectedloationName;
//     element.querySelector('#loationName').dispatchEvent(new Event('input'));
//     element.querySelector(
//       '#locationDescription',
//     ).value = expectedlocationDescription;
//     element
//       .querySelector('#locationDescription')
//       .dispatchEvent(new Event('input'));

//     fixture.detectChanges();

//     component.submitted.subscribe(
//       ({loationCustomId, loationName, locationDescription}) => {
//         expect(loationCustomId).toEqual(expectedloationCustomId);
//         expect(loationName).toEqual(expectedloationName);
//         expect(locationDescription).toEqual(expectedlocationDescription);
//       },
//     );

//     element.querySelector('button[type="submit"]').click();
//   });
// });
