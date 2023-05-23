import {UserModel} from "./user.model";
import {PlugModel} from "./plug.model";
import {ReviewStatusEnum} from "./enums/review-status.enum";
import {ChargingStationModel} from "./charging-station.model";

export interface ReviewModel {
  id: string;
  comments: string;
  rating: number;
  status: ReviewStatusEnum;
  timestamp: number;
  userObj: UserModel;
  chargingStationObj: ChargingStationModel;
}
