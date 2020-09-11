import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {SearchBar} from 'src/app/shared/search-bar';
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
export class DashboardHeaderComponent extends UnsubscribeOnDestroyAdapter {
  navLinks: NavLink[];
  showNavBar = true;
  searchBar: SearchBar | undefined;
  constructor(private readonly router: Router) {
    super();
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
    this.subs.add(
      router.events.subscribe(() => {
        for (const navLink of this.navLinks) {
          if (navLink.link === this.router.url) {
            this.showNavBar = true;
            break;
          } else {
            this.showNavBar = false;
          }
        }
      }),
    );
  }
  onActivate(componentRef: SearchBar): void {
    this.searchBar = componentRef;
  }
  search(query: string): void {
    if (this.searchBar) this.searchBar.search(query);
  }
}
