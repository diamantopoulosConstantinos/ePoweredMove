import {Pipe, PipeTransform} from "@angular/core";
import {ReviewStatusEnum} from "../models/enums/review-status.enum";

@Pipe({name: 'reviewStatus'})
export class ReviewStatusPipe implements PipeTransform {
  transform(value: string | null | undefined): string {

    switch(value) {
      case null: return 'Unknown'
      case undefined: return 'Unknown'
      case ReviewStatusEnum[ReviewStatusEnum.ACTIVE]: return 'Active'
      case ReviewStatusEnum[ReviewStatusEnum.CANCELLED]: return 'Cancelled'
      default: return 'Unknown'
    }
  }
}
