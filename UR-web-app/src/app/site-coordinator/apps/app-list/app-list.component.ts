import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {AppsService} from '../shared/apps.service';
import {ManageApps, App} from '../shared/app.model';
import {Permission} from 'src/app/shared/permission-enums';
@Component({
  selector: 'app-app-list',
  templateUrl: './app-list.component.html',
})
export class AppListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  manageApp$: Observable<ManageApps> = of();
  appUsersMessageMapping: {[k: string]: string} = {
    '=0': 'No App Users',
    '=1': 'One App User',
    'other': '# App Users',
  };
  studiesMessageMapping: {[k: string]: string} = {
    '=0': 'No Studies',
    '=1': 'One Study',
    'other': '# Studies',
  };

  constructor(private readonly appService: AppsService) {}

  ngOnInit(): void {
    this.getApps();
  }

  getApps(): void {
    this.manageApp$ = combineLatest(
      this.appService.getUserApps(),
      this.query$,
    ).pipe(
      map(([manageApps, query]) => {
        manageApps.apps = manageApps.apps.filter(
          (app: App) =>
            app.name.toLowerCase().includes(query.toLowerCase()) ||
            app.customId.toLowerCase().includes(query.toLowerCase()),
        );
        return manageApps;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim());
  }
  progressBarColor(app: App): string {
    if (app.enrollmentPercentage < 30) {
      return 'red__text__sm';
    } else if (app.enrollmentPercentage < 70) {
      return 'orange__text__sm';
    } else {
      return 'green__text__sm';
    }
  }
  checkEditPermission(permission: number): boolean {
    return permission === Permission.ViewAndEdit;
  }
}
