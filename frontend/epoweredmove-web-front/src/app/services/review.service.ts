import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {ReviewModel} from "../models/review.model";

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private readonly baseURL: string;

  constructor(private httpClient: HttpClient) {
    this.baseURL = environment.baseURL + "review";
  }

  getReviewsByChargingStation(chargingStationId: string): Observable<ReviewModel[]> {
    return this.httpClient.get(this.baseURL + "/allByChargingStation?chargingStationId=" + chargingStationId) as Observable<ReviewModel[]>;
  }
}
