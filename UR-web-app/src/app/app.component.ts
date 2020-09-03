import {Component, OnInit} from '@angular/core';
import {CookieService} from 'ngx-cookie-service';
import {v4 as uuidv4} from 'uuid';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  constructor(public cookieService: CookieService) {}

  title = 'User Registration Web App';
  ngOnInit() {
    if (!this.cookieService.get('correlationId')) {
      this.cookieService.set('correlationId', uuidv4());
      this.cookieService.set('appId', 'PARTICIPANT-MANAGER');
    }
  }
}
