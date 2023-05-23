import {Component, OnInit} from '@angular/core';
import {LoaderComponent} from "../loader/loader.component";
import {AuthService} from "../../services/auth.service";
import {RolesModel} from "../../models/roles.model";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit{
  isLoggedIn: boolean = false;
  loggedInName: string | null | undefined = "";
  loggedInRoles: RolesModel[] = [];
  constructor(private authService: AuthService) {
  }

  ngOnInit(){
    this.authService.isLoggedInChange.subscribe(value => {
      this.isLoggedIn = value;
      this.loggedInName = this.authService.loggedInUser?.displayName;
      this.loggedInRoles = this.authService.loggedInUserRoles;
    });
  }

  logout(){
    this.authService.logout();
  }

  hasRole(roleName: string): boolean {
    return this.loggedInRoles.some(r => roleName === r.name);
  }

  hasManagementAccess(): boolean {
    let isOwner = this.hasRole('POI_OWNER');
    let isAdmin = this.hasRole('ADMIN')
    return isOwner || isAdmin;
  }
}
