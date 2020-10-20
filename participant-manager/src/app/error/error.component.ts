import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GenericErrorCode, getGenericMessage } from '../shared/generic.error.codes.enum';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit {
errorCode='';
errorMessage='';

  constructor(    private readonly route: ActivatedRoute,
) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
        if (params['errorCode']) {
          this.errorCode = params.errorCode as string;
        }
        this.errorMessage=getGenericMessage(this.errorCode as GenericErrorCode);
      })
  }
}
