export interface Site {
  edit?: number;
  enrolled: number;
  enrollmentPercentage: number;
  status?: string;
  id: string;
  name: string;
  invited?: number;
}
export interface UpdateTargetEnrollmentRequest {
  targetEnrollment: number;
}
