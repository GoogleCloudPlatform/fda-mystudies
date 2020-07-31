import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {AppsService} from '../shared/apps.service';
import {App} from '../shared/app.model';
@Component({
  selector: 'app-app-list',
  templateUrl: './app-list.component.html',
  styleUrls: ['./app-list.component.scss'],
})
export class AppListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  app$: Observable<App[]> = of([]);
  apps: App[] = [];
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
    this.app$ = combineLatest(this.appService.getApps(), this.query$).pipe(
      map(([apps, query]) => {
        this.apps = apps;
        return this.apps.filter(
          (app: App) =>
            app.name.toLowerCase().includes(query.toLowerCase()) ||
            app.customId.toLowerCase().includes(query.toLowerCase()),
        );
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
}
