import {RolesModel} from "./roles.model";

export interface UserModel {
  id: string;
  name: string;
  surname: string;
  email: string;
  phone: string;
  password: string;
  roles: RolesModel[];
  selectedRoleNames: string[];
}
