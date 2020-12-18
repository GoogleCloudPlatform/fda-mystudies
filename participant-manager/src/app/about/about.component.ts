import {Component} from '@angular/core';
import {environment} from '@environment';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  title = '';
  description = '';
  constructor() {
    this.title = environment.aboutPageTitle;
    this.description = environment.aboutPageDescription;
  }
}
