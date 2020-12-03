import {Component} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {environment} from '@environment';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  safeHtmlContent: SafeHtml;
  constructor(sanitizer: DomSanitizer) {
    this.safeHtmlContent = sanitizer.bypassSecurityTrustHtml(
      environment.aboutPageHtmlContent,
    );
  }
}
