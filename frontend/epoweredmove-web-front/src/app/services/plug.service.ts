import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {AngularFireStorage} from "@angular/fire/compat/storage";
import {environment} from "../../environments/environment";
import {PoiModel} from "../models/poi.model";
import {Observable} from "rxjs";
import {PlugModel} from "../models/plug.model";
import {AvailabilityEnum} from "../models/enums/availability.enum";

@Injectable({
  providedIn: 'root'
})
export class PlugService {
  private readonly baseURL: string;
  constructor(private httpClient: HttpClient) {
    this.baseURL = environment.baseURL + "plug";
  }
  getPlug(plugId: string): Observable<PlugModel>{
    return this.httpClient.get(this.baseURL + "?id=" + plugId) as Observable<PlugModel>;
  }

  getPlugsByChargingStation(chargingStationId: string): Observable<PlugModel[]>{
    return this.httpClient.get(this.baseURL + "/allByChargingStation?chargingStationId=" + chargingStationId) as Observable<PlugModel[]>;
  }

  addPlug(plug: PlugModel): Observable<PlugModel>{
    return this.httpClient.post(this.baseURL, plug) as Observable<PlugModel>;
  }

  deletePlug(plugId: string): Observable<boolean> {
    return this.httpClient.delete(this.baseURL + "?id=" + plugId) as Observable<boolean>;
  }

  updatePlug(plug: PlugModel): Observable<PlugModel>{
    return this.httpClient.put(this.baseURL, plug) as Observable<PlugModel>;
  }


}
