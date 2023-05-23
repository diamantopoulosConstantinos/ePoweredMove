import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {AuthService} from "../services/auth.service";
import {AlertComponent} from "../components/alert/alert.component";

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private authService: AuthService,
              private router: Router,
              private alertMessage: AlertComponent){}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    let roles = route.data['roles'] as Array<string>;
    const loggedInUserRoles = this.authService.loggedInUserRoles;
    if(loggedInUserRoles && loggedInUserRoles.some(r => roles.includes(r.name))){
      return true;
    }else{
      //this.alertMessage.notificationMessage("You don't have the proper role to view this page");
      this.router.navigate(['']);
      return false;
    }
  }

}
