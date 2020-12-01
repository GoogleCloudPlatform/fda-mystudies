import {Component} from '@angular/core';
import {environment} from '@environment';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Component({
  selector: 'app-terms',
  templateUrl: './terms.component.html',
  styleUrls: ['./terms.component.scss'],
})
export class TermsComponent {
   safeHtmlContent:SafeHtml;
  constructor(sanitizer: DomSanitizer) {
      this.safeHtmlContent = sanitizer.bypassSecurityTrustHtml(environment.termsPageHtmlContent);
    }
}
