export interface DashboardModel {
  appId: string;
  appInfoId: number;
  customId: string;
  enrolledCount: number;
  enrollmentPercentage: number;
  id: number;
  invitedCount: number;
  name: string;
  sites: Site[];
  studyPermission: number;
  totalSitesCount: number;
}
export interface Site {
  edit: number;
  enrolledCount: number;
  enrollmentPercentage: number;
  id: number;
  invitedCount: number;
  name: string;
  status: string;
}
