import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from "@angular/material/paginator";
import {MatTableDataSource} from "@angular/material/table";
import {UserModel} from "../../models/user.model";
import {UserService} from "../../services/user.service";
import {AuthService} from "../../services/auth.service";
import {UserRolesPipe} from "../../pipes/user.roles.pipe";
import {RolesModel} from "../../models/roles.model";
import {UserRolesEnum} from "../../models/enums/user.roles.enum";
import {AvailabilityEnum} from "../../models/enums/availability.enum";

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.css']
})
export class AccountsComponent implements AfterViewInit, OnInit {
  displayedColumns: string[] = ['surname', 'name', 'email', 'phone', 'roles'];
  dataSource = new MatTableDataSource<UserModel>();

  @ViewChild(MatPaginator) paginator: MatPaginator;
  allRoles: RolesModel[] = [];

  constructor(private userService: UserService,
              private authService: AuthService,
              public userRolePipe: UserRolesPipe) {
    for (let role in UserRolesEnum) {
      if (isNaN(Number(role))) {
        const currentRole = {} as RolesModel;
        currentRole.name = role;
        this.allRoles.push(currentRole);
      }
    }
  }

  ngOnInit() {
    //if page is refreshed wait for auth loading
    if(!this.authService.isLoggedIn){
      this.authService.isLoggedInChange.subscribe(value => {
        if(value){
          this.fetchUsers();
        }
      });
    }
    else{
      this.fetchUsers();
    }

  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  fetchUsers(){
    this.userService.getUsers().subscribe({
      next: users => {
        users.forEach(user =>{
          user.selectedRoleNames = [];
          user.roles.forEach(role => {
            user.selectedRoleNames.push(role.name);
          });
        });
        this.dataSource.data = users;
      }
    });
  }


  changeRoles(userId: string, roleNames: string[]) {
    const roles: RolesModel[] = [];
    roleNames.forEach(roleName => {
      const role = {} as RolesModel;
      role.name = roleName;
      roles.push(role);
    })
    this.userService.updateRoles(userId, roles).subscribe();
  }

  selectedRoleNames(roles: RolesModel[]): string[] {
    const roleNames: string[] = [];
    roles.forEach(role => {
      roleNames.push(role.name);
    });
    return roleNames;
  }
}
