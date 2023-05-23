import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import { AngularFireStorage } from '@angular/fire/compat/storage';
import {ImageResponseModel} from "../models/image.response.model";
import {ChargingStationModel} from "../models/charging-station.model";
import {PoiModel} from "../models/poi.model";

@Injectable({
  providedIn: 'root'
})
export class ChargingStationService {
  private readonly baseURL: string;
  constructor(private httpClient: HttpClient,
              private storage: AngularFireStorage) {
    this.baseURL = environment.baseURL + "chargingStation";
  }
  addChargingStationImage(chargingStationId: string, file: File): Observable<ImageResponseModel>{
    const formData: FormData = new FormData();
    formData.append('file', file);
    return this.httpClient.post(this.baseURL + "/addImage?id=" + chargingStationId, formData) as Observable<ImageResponseModel>;
  }

  getChargingStationImage(imageId: string): Observable<string> {
    return this.storage.ref("charging-stations").child(imageId).getDownloadURL() as Observable<string>;
  }
}
