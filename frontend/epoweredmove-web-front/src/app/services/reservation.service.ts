import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {ReviewModel} from "../models/review.model";
import {AuthService} from "./auth.service";
import {ReservationModel} from "../models/reservation.model";
import {PlugModel} from "../models/plug.model";

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private readonly baseURL: string;

  constructor(private httpClient: HttpClient,
              private authService: AuthService) {
    this.baseURL = environment.baseURL + "reservation";
  }

  getReservations(): Observable<ReservationModel[]> {
    return this.httpClient.get(this.baseURL + "/all") as Observable<ReservationModel[]>;
  }

  getReservationsByChargingStationOwner(): Observable<ReservationModel[]> {
    const userId = this.authService.loggedInUser?.uid;
    return this.httpClient.get(this.baseURL + "/allByChargingStationOwner?userId=" + userId) as Observable<ReservationModel[]>;
  }

  updateReservation(reservation: ReservationModel): Observable<ReservationModel>{
    return this.httpClient.put(this.baseURL, reservation) as Observable<ReservationModel>;
  }
}
