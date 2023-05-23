import {UserModel} from "./user.model";
import {PlugTypeModel} from "./plug-type.model";
import {VehicleTypeEnum} from "./enums/vehicle-type.enum";
import {EvTypeEnum} from "./enums/ev-type.enum";

export interface VehicleModel {
  id: string;
  brand: string;
  vehicleType: VehicleTypeEnum;
  evType: EvTypeEnum;
  model: string;
  releaseYear: number;
  usableBatterySize: number;
  plugTypeId: string;
  plugTypeObj: PlugTypeModel;
  avgConsumption: string;
  userId: string;
  userObj: UserModel;
  timestamp: number;
}
