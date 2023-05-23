import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {AlertComponent} from "../components/alert/alert.component";
import {MainComponent} from "../components/main/main.component";
import {AuthService} from "./auth.service";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {UserModel} from "../models/user.model";
import {fromPromise} from "rxjs/internal/observable/innerFrom";
import {RolesModel} from "../models/roles.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly baseURL: string;
  constructor(private httpClient: HttpClient) {
    this.baseURL = environment.baseURL + "user";
  }

  getUser(userId: string): Observable<UserModel>{
    return this.httpClient.get(this.baseURL + "?id=" +userId) as Observable<UserModel>;
  }

  getUserRoles(userId: string): Observable<RolesModel[]>{
    return this.httpClient.get(this.baseURL + "/roles?id=" + userId) as Observable<RolesModel[]>;
  }

  getUsers(): Observable<UserModel[]>{
    return this.httpClient.get(this.baseURL + "/all") as Observable<UserModel[]>;
  }

  updateRoles(userId: string, roles: RolesModel[]): Observable<RolesModel[]> {
    return this.httpClient.put(this.baseURL + "/roles?id=" + userId, roles) as Observable<RolesModel[]>;
  }
}
