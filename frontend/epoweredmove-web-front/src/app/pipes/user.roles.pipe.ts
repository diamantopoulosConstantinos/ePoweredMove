import {Pipe, PipeTransform} from "@angular/core";
import {UserRolesEnum} from "../models/enums/user.roles.enum";

@Pipe({name: 'userRoles'})
export class UserRolesPipe implements PipeTransform {
  transform(value: string | null | undefined): string {

    switch(value) {
      case null: return '-'
      case undefined: return '-'
      case UserRolesEnum[UserRolesEnum.POI_OWNER]: return 'Point of Interest Owner'
      case UserRolesEnum[UserRolesEnum.ADMIN]: return 'Admin'
      case UserRolesEnum[UserRolesEnum.USER]: return 'User'
      default: return '-'
    }
  }
}
