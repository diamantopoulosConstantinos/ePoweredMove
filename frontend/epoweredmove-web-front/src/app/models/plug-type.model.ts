import {PaymentTypeEnum} from "./enums/payment-type.enum";
import {PoiModel} from "./poi.model";
import {AvailabilityEnum} from "./enums/availability.enum";
import {ChargingStationModel} from "./charging-station.model";
import {TeslaCompatibleEnum} from "./enums/tesla-compatible.enum";
import {CurrentTypeEnum} from "./enums/current-type.enum";
import {PlugModel} from "./plug.model";

export interface PlugTypeModel {
  id: string;
  connector: string;
  current: CurrentTypeEnum;
  typeLevel: string;
  description: string;
  compatibility: string;
  tesla: TeslaCompatibleEnum;
  imageId: string;
  imageUrl: string;

}
