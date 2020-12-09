import {Component, OnInit} from '@angular/core';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {ActivatedRoute} from '@angular/router';
import {UserService} from '../shared/user.service';
import {User} from 'src/app/entity/user';
import {ManageUserDetails} from '../shared/manage-user-details';
import {Permission} from 'src/app/shared/permission-enums';
import {Status} from 'src/app/shared/enums';

@Component({
  selector: 'user-details',
  templateUrl: './user-details.component.html',
})
export class UserDetailsComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  adminId = '';
  user = {} as User;
  permission = Permission;
  sitesMessageMapping: {[k: string]: string} = {
    '=0': '0 Sites',
    '=1': '1 Site',
    'other': '# Sites',
  };
  onBoardingStatus = Status;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly userService: UserService,
  ) {
    super();
  }

  ngOnInit(): void {
    this.subs.add(
      this.route.params.subscribe((params) => {
        if (params.userId) {
          this.adminId = params.userId as string;
        }
        this.getUserDetails();
      }),
    );
  }

  getUserDetails(): void {
    this.subs.add(
      this.userService
        .getUserDetails(this.adminId)
        .subscribe((data: ManageUserDetails) => {
          this.user = data.user;
          this.user.manageLocationsSelected =
            this.user.manageLocations !== null;
        }),
    );
  }
  statusColour(status: string | undefined): string {
    if (status === this.onBoardingStatus.Active) {
      return 'txt__green';
    } else if (status === this.onBoardingStatus.Deactivated) {
      return 'txt__light-gray';
    } else {
      return 'txt__space-gray';
    }
  }
}
