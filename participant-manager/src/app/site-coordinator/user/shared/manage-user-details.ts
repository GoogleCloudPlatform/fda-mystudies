import {User} from 'src/app/entity/user';

export interface ManageUserDetails {
  user: User;
}
export interface UpdateStatusRequest {
  status: number;
}
