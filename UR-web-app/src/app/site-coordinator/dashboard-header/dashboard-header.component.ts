import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
export interface NavLink {
  label: string;
  link: string;
  index: number;
}
@Component({
  selector: 'app-dashboard-header',
  templateUrl: './dashboard-header.component.html',
  styleUrls: ['./dashboard-header.component.scss'],
})
export class DashboardHeaderComponent implements OnInit {
  navLinks: NavLink[];
  activeLinkIndex = 0;
  showNavBar = true;
  constructor(private readonly router: Router) {
    this.navLinks = [
      {
        label: 'Sites',
        link: '/coordinator/studies/sites',
        index: 0,
      },
      {
        label: 'Studies',
        link: '/coordinator/studies',
        index: 1,
      },
      {
        label: 'Apps',
        link: '/coordinator/apps',
        index: 2,
      },
    ];
  }
  ngOnInit(): void {
    this.router.events.subscribe(() => {
      for (const navLink of this.navLinks) {
        if (navLink.link === this.router.url) {
          this.showNavBar = true;
          this.activeLinkIndex = this.navLinks.indexOf(navLink);
          break;
        } else {
          this.showNavBar = false;
        }
      }
    });
  }
}
