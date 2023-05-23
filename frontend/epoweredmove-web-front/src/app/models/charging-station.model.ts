import {PoiModel} from "./poi.model";
import {PlugModel} from "./plug.model";

export interface ChargingStationModel {
  id: string;
  pricePerKwh: number;
  autoAccept: boolean;
  barcodeEnabled: boolean;
  apiEnabled: boolean;
  imageId: string;
  plugsObj: PlugModel[];
  imageUrl: string;

}
