import {Pipe, PipeTransform} from "@angular/core";
import {ReviewStatusEnum} from "../models/enums/review-status.enum";
import {PaymentTypeEnum} from "../models/enums/payment-type.enum";

@Pipe({name: 'paymentMethod'})
export class PaymentTypePipe implements PipeTransform {
  transform(value: string | null | undefined): string {

    switch(value) {
      case null: return 'Unknown'
      case undefined: return 'Unknown'
      case PaymentTypeEnum[PaymentTypeEnum.APP]: return 'App'
      case PaymentTypeEnum[PaymentTypeEnum.PAYPAL]: return 'Paypal'
      case PaymentTypeEnum[PaymentTypeEnum.LOCALLY]: return 'Locally'
      case PaymentTypeEnum[PaymentTypeEnum.EBANKING]: return 'E-Banking'
      default: return 'Unknown'
    }
  }
}
