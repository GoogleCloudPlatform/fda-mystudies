import {User} from 'src/app/entity/user';
import {ApiResponse} from 'src/app/entity/api.response.model';

export interface ManageUsers extends ApiResponse {
  users: User[];
}
