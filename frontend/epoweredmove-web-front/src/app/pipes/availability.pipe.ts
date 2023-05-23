import {Pipe, PipeTransform} from "@angular/core";
import {AvailabilityEnum} from "../models/enums/availability.enum";

@Pipe({name: 'availability'})
export class AvailabilityPipe implements PipeTransform {
  transform(value: string | null | undefined): string {

    switch(value) {
      case null: return 'Unknown'
      case undefined: return 'Unknown'
      case AvailabilityEnum[AvailabilityEnum.AVAILABLE]: return 'Available'
      case AvailabilityEnum[AvailabilityEnum.INUSE]: return 'In Use'
      case AvailabilityEnum[AvailabilityEnum.UNKNOWN]: return 'Unknown'
      case AvailabilityEnum[AvailabilityEnum.UNAVAILABLE]: return 'Unavailable'
      default: return 'Unknown'
    }
  }
}
