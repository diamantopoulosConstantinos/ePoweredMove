import {Injectable, OnInit} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import { AuthService } from "../services/auth.service";
import {AlertComponent} from "../components/alert/alert.component";

@Injectable()
export class IsNotAuthGuard implements CanActivate{
  constructor(private authService: AuthService,
              private router: Router,
              private alertMessage: AlertComponent){
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    if(this.authService.getLoggedInState()){
      this.alertMessage.notificationMessage('You are already logged in. You are redirected to Home Page');
      this.router.navigate(['']);
      return false;
    }
    else{

      return true;
    }
  }
}
