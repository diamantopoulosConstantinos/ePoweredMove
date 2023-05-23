import {UserModel} from "./user.model";
import {PlugModel} from "./plug.model";
import {VehicleModel} from "./vehicle.model";

export interface ReservationModel {
  id: string;
  accepted: boolean;
  timeStart: number;
  timeEnd: number;
  vehicleId: string;
  vehicleObj: VehicleModel;
  userId: string;
  userObj: UserModel;
  plugId: string;
  plugObj: PlugModel;
  timestamp: number;
  cancelled: boolean;
  poiLatitude: number;
  poiLongitude: number;
}
