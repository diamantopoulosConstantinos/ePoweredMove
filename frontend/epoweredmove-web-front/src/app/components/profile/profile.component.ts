import { Component } from '@angular/core';
import {UserService} from "../../services/user.service";
import {AlertComponent} from "../alert/alert.component";
import {UserModel} from "../../models/user.model";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
  user: UserModel|null = null;
  constructor(private userService: UserService,
              private authService: AuthService,
              private alert: AlertComponent) {
    //if page is refreshed wait for auth loading
    if(!this.authService.isLoggedIn){
      this.authService.isLoggedInChange.subscribe(value => {
        if(value){
          this.fetchUser();
        }
      });
    }
    else{
      this.fetchUser();
    }
  }

  fetchUser(){
    this.userService.getUser(this.authService.loggedInUser!!.uid).subscribe({
      next: user => {
        if(user){
          this.user = user;
        }
      },
      error: err => {
        this.alert.failureMessage("Couldn't get logged in user's data");
      }
    });
  }
}
