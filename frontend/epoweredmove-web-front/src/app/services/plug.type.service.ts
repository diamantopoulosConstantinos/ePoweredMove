import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {AngularFireStorage} from "@angular/fire/compat/storage";
import {environment} from "../../environments/environment";
import {PlugModel} from "../models/plug.model";
import {PlugTypeModel} from "../models/plug-type.model";

@Injectable({
  providedIn: 'root'
})
export class PlugTypeService {
  private readonly baseURL: string;

  constructor(private httpClient: HttpClient,
              private storage: AngularFireStorage) {
    this.baseURL = environment.baseURL + "plugType";
  }

  getPlugTypes(): Observable<PlugTypeModel[]>{
    return this.httpClient.get(this.baseURL + "/all") as Observable<PlugTypeModel[]>;
  }

  getPlugTypeImage(imageId: string): Observable<string> {
    return this.storage.ref("plug-types").child(imageId).getDownloadURL() as Observable<string>;
  }
}
