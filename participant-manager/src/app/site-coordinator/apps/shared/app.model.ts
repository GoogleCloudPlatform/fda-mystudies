export interface App {
  permission: number;
  appUsersCount: number;
  customId?: string;
  enrolledCount: number;
  enrollmentPercentage: number;
  id: string;
  invitedCount: number;
  name?: string;
  studiesCount: number;
}
export interface ManageApps {
  apps: App[];
  studyPermissionCount: number;
  superAdmin: boolean;
}
