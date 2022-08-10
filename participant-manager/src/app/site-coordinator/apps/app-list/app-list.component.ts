import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {AppsService} from '../shared/apps.service';
import {ManageApps, App} from '../shared/app.model';
import {Permission} from 'src/app/shared/permission-enums';
import {Status} from 'src/app/shared/enums';
import {SearchService} from 'src/app/shared/search.service';
import {ToastrService} from 'ngx-toastr';
import {SearchParameterService} from 'src/app/service/search-parameter.service';
const limit = 10;
@Component({
  selector: 'app-app-list',
  templateUrl: './app-list.component.html',
  styleUrls: ['./app-list.component.scss'],
})
export class AppListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  manageApp$: Observable<ManageApps> = of();
  appList: App[] = [];
  appStatus = Status;
  manageAppsBackup = {} as ManageApps;
  appUsersMessageMapping: {[k: string]: string} = {
    '=0': 'No app users',
    '=1': 'One app user',
    'other': '# app users',
  };
  studiesMessageMapping: {[k: string]: string} = {
    '=0': 'No studies',
    '=1': 'One study',
    'other': '# studies',
  };
  loadMoreEnabled = true;

  searchValue = '';

  constructor(
    private readonly appService: AppsService,
    private readonly sharedService: SearchService,
    private readonly toastr: ToastrService,
    private readonly searchParameter: SearchParameterService,
  ) {}

  ngOnInit(): void {
    this.searchParameter.setSearchParameter('');
    this.searchParameter.searchParam$.subscribe((updatedParameter) => {
      this.manageAppsBackup = {} as ManageApps;
      this.searchValue = updatedParameter;
      this.getApps();
    });
    this.sharedService.updateSearchPlaceHolder('Search by app ID or name');
  }
  getApps(): void {
    this.manageApp$ = combineLatest(
      this.appService.getUserApps(limit, 0, this.searchValue),
      this.query$,
    ).pipe(
      map(([manageApps]) => {
        this.manageAppsBackup = {...manageApps};
        const app = [];
        this.manageAppsBackup = {...manageApps};
        app.push(...manageApps.apps);
        this.appList = app;
        this.loadMoreEnabled =
          (this.manageAppsBackup.apps.length % limit === 0 ? true : false) &&
          manageApps.apps.length > 0;
        return this.manageAppsBackup;
      }),
    );
  }

  progressBarColor(app: App): string {
    if (app.enrollmentPercentage && app.enrollmentPercentage > 70) {
      return 'green__text__sm';
    } else if (
      app.enrollmentPercentage &&
      app.enrollmentPercentage >= 30 &&
      app.enrollmentPercentage <= 70
    ) {
      return 'orange__text__sm';
    } else {
      return 'red__text__sm';
    }
  }
  checkEditPermission(permission: number): boolean {
    return permission === Permission.ViewAndEdit;
  }
  checkViewPermission(permission: number): boolean {
    return (
      permission === Permission.View || permission === Permission.ViewAndEdit
    );
  }

  loadMoreSites(): void {
    const offset = this.manageAppsBackup.apps.length;
    this.appService
      .getUserApps(limit, offset, this.searchValue)
      .subscribe((manageApps) => {
        const apps = [];
        apps.push(...this.manageAppsBackup.apps);
        apps.push(...manageApps.apps);
        this.appList = apps;
        this.manageAppsBackup.apps = apps;
        if (!manageApps.superAdmin && manageApps.studyPermissionCount < 2) {
          this.toastr.error(
            'This view displays app-wise enrollment if you manage multiple studies.',
          );
        }
        this.loadMoreEnabled =
          (this.manageAppsBackup.apps.length % limit === 0 ? true : false) &&
          manageApps.apps.length > 0;
      });
  }
}
