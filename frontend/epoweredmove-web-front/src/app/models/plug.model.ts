import {AvailabilityEnum} from "./enums/availability.enum";
import {ChargingStationModel} from "./charging-station.model";
import {PlugTypeModel} from "./plug-type.model";

export interface PlugModel {
  id: string;
  availability: string;
  power: number;
  phases: number;
  chargingStationId: string;
  chargingStationObj: ChargingStationModel;
  timestamp: number;
  plugTypeId: string;
  plugTypeObj: PlugTypeModel;
}
