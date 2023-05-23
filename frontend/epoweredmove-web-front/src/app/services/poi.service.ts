import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Router} from "@angular/router";
import {AlertComponent} from "../components/alert/alert.component";
import {MainComponent} from "../components/main/main.component";
import {AuthService} from "./auth.service";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {UserModel} from "../models/user.model";
import {PoiModel} from "../models/poi.model";

@Injectable({
  providedIn: 'root'
})
export class PoiService {
  private readonly baseURL: string;
  constructor(private httpClient: HttpClient,
              private authService: AuthService) {
    this.baseURL = environment.baseURL + "poi";
  }

  addPoi(poi: PoiModel): Observable<PoiModel>{
    return this.httpClient.post(this.baseURL, poi) as Observable<PoiModel>;
  }

  getPoi(poiId: string): Observable<PoiModel>{
    return this.httpClient.get(this.baseURL + "?id=" + poiId) as Observable<PoiModel>;
  }

  getPois(): Observable<PoiModel[]>{
    return this.httpClient.get(this.baseURL + "/all") as Observable<PoiModel[]>;
  }

  getUserPois(): Observable<PoiModel[]>{
    const loggedInUserId = this.authService.loggedInUser?.uid;
    return this.httpClient.get(this.baseURL + "/allByUser?userId=" + loggedInUserId) as Observable<PoiModel[]>;
  }

  deletePoi(poiId: string): Observable<boolean> {
    return this.httpClient.delete(this.baseURL + "?id=" + poiId) as Observable<boolean>;
  }

  updatePoi(poi: PoiModel): Observable<PoiModel> {
    return this.httpClient.put(this.baseURL, poi) as Observable<PoiModel>;
  }
}
