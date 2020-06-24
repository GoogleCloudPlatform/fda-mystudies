import {Component, OnInit, ViewChild, TemplateRef} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '../shared/location.model';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {NgForm, NgModel} from '@angular/forms';

@Component({
  selector: 'location-add',
  templateUrl: './add-location.component.html',
  styleUrls: ['./add-location.component.scss'],
})
export class AddLocationComponent implements OnInit {
  @ViewChild('addLocationForm') addLocationForm: ngFormg;
  location: Location = new Location();
  errorMessage = '';
  successMessage = '';
  modalRef: BsModalRef;
  constructor(
    private router: Router,
    private modalService: BsModalService,
    private locationService: LocationService,
    private toastr: ToastrService,
  ) {}
  openModal(template: TemplateRef<any>) {
    this.modalRef = this.modalService.show(template);
  }
  ngOnInit() {}

  addLocation() {
    this.errorMessage = '';
    this.successMessage = '';
    this.locationService.addLocation(this.location).subscribe(
      (data) => {
        this.toastr.success(data.successBean.message);
        // this.successMessage = "Location Added successfully."
      },
      (error) => {
        console.log(error);
        this.toastr.error(error.error.userMessage);
        //this.errorMessage = error.error.message;
      },
    );
  }
}
