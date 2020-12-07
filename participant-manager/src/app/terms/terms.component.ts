import {Component} from '@angular/core';
import {environment} from '@environment';

@Component({
  selector: 'app-terms',
  templateUrl: './terms.component.html',
  styleUrls: ['./terms.component.scss'],
})
export class TermsComponent {
  title='';
  description='';
  constructor() {
    this.title =environment.termsPageTitle;
    this.description =environment.termsPageDescription;
  }
}
