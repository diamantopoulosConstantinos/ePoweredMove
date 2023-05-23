import {PaymentTypeEnum} from "./enums/payment-type.enum";
import {PoiModel} from "./poi.model";

export interface PaymentMethodModel {
  id: string;
  description: string;
  poiId: string;
}
