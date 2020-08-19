export interface Site {
  edit: number;
  enrolledCount: number;
  enrollmentPercentage: number;
  id: number;
  invitedCount: number;
  name: string;
  status: string;
}
export interface UpdateTargetEnrollmentRequest {
  targetEnrollment: number;
}
