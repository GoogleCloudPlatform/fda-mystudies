import {Component} from '@angular/core';
import {environment} from '@environment';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
})
export class FooterComponent {
  copyright = environment.copyright;
}
