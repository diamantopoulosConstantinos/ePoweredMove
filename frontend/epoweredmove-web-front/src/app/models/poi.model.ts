import {ChargingStationModel} from "./charging-station.model";
import {UserModel} from "./user.model";
import {PaymentMethodModel} from "./payment-method.model";

export interface PoiModel {
  id: string;
  latitude: number;
  longitude: number;
  parking: boolean;
  illumination: boolean;
  wc: boolean;
  shopping: boolean;
  food: boolean;
  phone: string;
  userObj: UserModel;
  chargingStationObj: ChargingStationModel;
  paymentMethodsObj: PaymentMethodModel[];
  timestamp: number;

}
