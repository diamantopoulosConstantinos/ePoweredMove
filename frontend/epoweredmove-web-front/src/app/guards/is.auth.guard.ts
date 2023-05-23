import {Injectable, OnInit} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import { AuthService } from "../services/auth.service";
import {AlertComponent} from "../components/alert/alert.component";

@Injectable()
export class IsAuthGuard implements CanActivate{
  constructor(private authService: AuthService,
              private router: Router,
              private alertMessage: AlertComponent){
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    if(this.authService.getLoggedInState()){
      return true;
    }
    else{
      this.alertMessage.failureMessage('You are not allowed to view this page. You are redirected to login Page');
      this.router.navigate(['signin']);
      return false;
    }
  }
}
