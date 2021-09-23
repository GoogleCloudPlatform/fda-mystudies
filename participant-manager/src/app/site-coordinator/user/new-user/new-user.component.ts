import {
  Component,
  ElementRef,
  OnInit,
  QueryList,
  ViewChildren,
} from '@angular/core';
import {UserService} from '../shared/user.service';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {AppDetails, App, Study, Site} from '../shared/app-details';
import {User} from 'src/app/entity/user';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {Permission} from 'src/app/shared/permission-enums';
import {AppsService} from '../../apps/shared/apps.service';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';

@Component({
  selector: 'user-new',
  templateUrl: './new-user.component.html',
  styleUrls: ['./new-user.component.scss'],
})
export class AddNewUserComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  appDetails = {} as AppDetails;
  appDetailsBackup = {} as AppDetails;
  selectedApps: App[] = [];
  user = {} as User;
  permission = Permission;
  sitesMessageMapping: {[k: string]: string} = {
    '=0': '0 sites',
    '=1': '1 site',
    'other': '# sites',
  };
  disableButton = false;
  @ViewChildren('permissionCheckBox')
  selectedPermission: QueryList<ElementRef> = new QueryList();
  constructor(
    private readonly router: Router,
    private readonly userService: UserService,
    private readonly appsService: AppsService,
    private readonly toastr: ToastrService,
  ) {
    super();
  }

  ngOnInit(): void {
    this.getAllApps();
  }

  getAllApps(): void {
    this.subs.add(
      this.appsService.getAllAppsWithStudiesAndSites().subscribe((data) => {
        this.appDetails = data;
        this.appDetailsBackup = JSON.parse(
          JSON.stringify(this.appDetails),
        ) as AppDetails;
      }),
    );
  }

  deleteAppFromList(appId: string): void {
    this.selectedApps = this.selectedApps.filter((obj) => obj.id !== appId);
  }

  appCheckBoxChange(app: App): void {
    if (app.selected) {
      app.permission = this.permission.View;
      app.selectedSitesCount = app.totalSitesCount;
    } else {
      app.permission = null;
      app.selectedSitesCount = 0;
    }
    app.studies.forEach((study) => {
      study.permission = app.permission as number;
      study.selected = app.selected;
      if (study.selected) {
        study.selectedSitesCount = study.totalSitesCount;
      } else {
        study.selectedSitesCount = 0;
      }
      study.sites.forEach((site) => {
        site.permission = study.permission;
        site.selected = study.selected;
      });
    });
  }

  appRadioButtonChange(app: App): void {
    app.studies.forEach((study) => {
      study.permission = app.permission;
      study.sites.forEach((site) => {
        site.permission = app.permission;
      });
    });
  }

  studyCheckBoxChange(study: Study, app: App): void {
    const alreadySelectedSitesCount = study.sites.filter((s) => s.selected)
      .length;
    if (!app.selectedSitesCount) {
      app.selectedSitesCount = 0;
    }
    if (study.selected) {
      study.permission = this.permission.View;
      app.selectedSitesCount =
        app.selectedSitesCount - alreadySelectedSitesCount + study.sites.length;
      study.selectedSitesCount = study.sites.length;
    } else {
      study.permission = null;
      app.selectedSitesCount = app.selectedSitesCount - study.sites.length;
      study.selectedSitesCount = 0;
    }
    study.sites.forEach((site) => {
      site.permission = study.permission;
      site.selected = study.selected;
    });
  }

  studyRadioButtonChange(study: Study): void {
    study.sites.forEach((site) => {
      site.permission = study.permission as number;
    });
  }

  siteCheckBoxChange(site: Site, study: Study, app: App): void {
    if (!study.selectedSitesCount) {
      study.selectedSitesCount = 0;
    }
    if (!app.selectedSitesCount) {
      app.selectedSitesCount = 0;
    }
    if (site.selected) {
      site.permission = this.permission.View;
      study.selectedSitesCount += 1;
      app.selectedSitesCount += 1;
    } else {
      site.permission = null;
      study.selectedSitesCount -= 1;
      app.selectedSitesCount -= 1;
    }
  }

  locationsCheckBoxChange(): void {
    if (this.user.manageLocationsSelected) {
      this.user.manageLocations = this.permission.View;
    } else {
      this.user.manageLocations = null;
    }
  }

  add(): void {
    const permissionsSelected = this.selectedPermission.filter((element) => {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      return element.nativeElement?.checked as boolean;
    });
    if (
      this.user.superAdmin ||
      this.user.manageLocationsSelected ||
      (this.selectedApps.length > 0 && permissionsSelected.length !== 0)
    ) {
      this.disableButton = true;
      if (this.user.superAdmin) {
        this.user.apps = [];
      } else {
        this.user.superAdmin = false;
        this.user.apps = this.selectedApps;
      }
      this.removeExtraAttributesFromApiRequest();
      this.userService.add(this.user).subscribe(
        (successResponse: ApiResponse) => {
          this.disableButton = false;
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else this.toastr.success('Success');
          void this.router.navigate(['/coordinator/users']);
        },
        () => {
          this.disableButton = false;
        },
      );
    } else {
      this.toastr.error(
        'Please assign the admin at least one permission from the permissions set shown.',
      );
      return;
    }
  }
  removeExtraAttributesFromApiRequest(): void {
    delete this.user.manageLocationsSelected;
  }
  superAdminCheckBoxChange(): void {
    if (this.user.superAdmin) {
      this.selectedApps = [];
      this.appDetails = this.appDetailsBackup;
      this.user.manageLocationsSelected = false;
      this.user.manageLocations = null;
    }
  }
}
