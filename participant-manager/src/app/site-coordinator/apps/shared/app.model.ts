import {Status} from 'src/app/shared/enums';
export interface App {
  permission: number;
  appUsersCount: number;
  customId?: string;
  enrolledCount: number;
  enrollmentPercentage: number;
  id: string;
  invitedCount: number;
  appStatus: Status;
  name?: string;
  studiesCount: number;
}
export interface ManageApps {
  apps: App[];
  studyPermissionCount: number;
  superAdmin: boolean;
  status: number;
}
