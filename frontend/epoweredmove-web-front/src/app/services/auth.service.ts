import {Injectable, OnInit} from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { environment } from "../../environments/environment";
import { AngularFireAuth } from '@angular/fire/compat/auth';
import {from, Observable, of, Subject} from "rxjs";
import {Router} from "@angular/router";
import {AlertComponent} from "../components/alert/alert.component";
import firebase from 'firebase/compat/app';
import {LoaderService} from "./loader.service";
import {UserModel} from "../models/user.model";
import {UserService} from "./user.service";
import {RolesModel} from "../models/roles.model";

@Injectable({
  providedIn: 'root'
})
export class AuthService{
  private readonly baseURL: string;
  loggedInUser: firebase.User | null = null;
  loggedInUserRoles: RolesModel[] = [];
  isLoggedIn: boolean = false;
  isLoggedInChange: Subject<boolean> = new Subject<boolean>();
  constructor(private httpClient: HttpClient,
              private router: Router,
              private alert: AlertComponent,
              private loadingService: LoaderService,
              private auth: AngularFireAuth,
              private userService: UserService) {
    this.baseURL = environment.baseURL + "user";
    this.isLoggedInChange.subscribe(value => {
      this.isLoggedIn = value;
    });
    this.auth.onAuthStateChanged(user => {
      this.loggedInUser = user;
      localStorage.setItem('loggedInState', user ? 'true' : 'false');
      if(user){
        this.userService.getUserRoles(this.loggedInUser!!.uid).subscribe({
          next: userRoles => {
            this.loggedInUserRoles = userRoles;
            this.refreshLoggedInStatus();
          },
          error: err => {
            this.loggedInUserRoles = [];
            this.refreshLoggedInStatus();
          }
        })
      }
      else{
        this.loggedInUserRoles = [];
        this.refreshLoggedInStatus();
      }
    });
  }


  createLocalUser(userModel: UserModel) {
    this.loadingService.setLoading(true);

    this.httpClient.post(this.baseURL + "/createUser", userModel).subscribe({
      next: res => {
        this.login(userModel.email, userModel.password);
      },
      error: err => {
        this.loadingService.setLoading(false);
        this.alert.failureMessage("Error occurred while creating user. Please try again");
      }
    });
  }

  logout(){
    this.loadingService.setLoading(true);
    this.auth.signOut()
      .then(() => {
        this.router.navigate(['']);
        this.loadingService.setLoading(false);
      })
      .catch(reason => {
        this.alert.failureMessage("Error occurred while signing out. Please try again");
        this.loadingService.setLoading(false);
      });
  }

  login(email: string, password: string) {
    this.loadingService.setLoading(true);
    this.auth.signInWithEmailAndPassword(email, password)
      .then(userCredential => {
        this.alert.signInMessage(userCredential.user!!.displayName);
        this.loadingService.setLoading(false);
        this.router.navigate(['']);
      }
    ).catch(reason => {
      this.alert.failureMessage("Wrong credentials. Please try again");
      this.loadingService.setLoading(false);
    });
  }

  loginGoogle() {
    this.loadingService.setLoading(true);
    this.auth.signInWithPopup(new firebase.auth.GoogleAuthProvider())
      .then(userCredential => {
          if(userCredential.user){
            const userMode = {} as UserModel;
            userMode.id = userCredential.user.uid ? userCredential.user.uid : '';
            userMode.name = userCredential.user.displayName ? userCredential.user.displayName : '';
            userMode.email = userCredential.user.email ? userCredential.user.email : '';
            userMode.phone = userCredential.user.phoneNumber ? userCredential.user.phoneNumber : '';
            this.googleLoginCreateUser(userMode).subscribe();
            this.alert.signInMessage(userCredential.user!!.displayName);
            this.loadingService.setLoading(false);
            this.router.navigate(['']);
          }
          else{
            this.alert.failureMessage("Something went wrong. Please try again");
            this.loadingService.setLoading(false);
          }
        }
      ).catch(reason => {
      this.alert.failureMessage("Wrong credentials. Please try again");
      this.loadingService.setLoading(false);
    });
  }

  refreshIdToken(): Promise<string | null>  {
    return new Promise((resolve, reject) => {
      localStorage.removeItem("idToken");
      from(this.auth.currentUser).subscribe({
        next: user => {
          if(user){
            from(user.getIdToken(true)).subscribe({
              next: token => {
                if(token){
                  localStorage.setItem("idToken", token);
                }
                resolve(token);
              },
              error: err => {
                resolve(null);
              }
            })
          }
          else{
            resolve(null);
          }
        },
        error: err => {
          resolve(null);
        }
      });
    });
  }

  refreshLoggedInStatus() {
    this.verifyLoggedInStatus().subscribe({
      next: value => {
        this.isLoggedInChange.next(value);
      },
      error: err => {
        this.isLoggedInChange.next(false);
      }
    })
  }

  googleLoginCreateUser(userModel: UserModel): Observable<boolean>{
    return this.httpClient.post(this.baseURL + "/googleSignIn", userModel) as Observable<boolean>;
  }

  verifyLoggedInStatus(): Observable<boolean>{
    return this.httpClient.post(this.baseURL + "/verifyToken", "") as Observable<boolean>;
  }

  getLoggedInState(): boolean {
    const state = localStorage.getItem('loggedInState');
    if(state){
      return state === 'true';
    }
    return false;
  }
}
