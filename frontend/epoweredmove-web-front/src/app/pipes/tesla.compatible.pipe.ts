import {Pipe, PipeTransform} from "@angular/core";
import {AvailabilityEnum} from "../models/enums/availability.enum";
import {TeslaCompatibleEnum} from "../models/enums/tesla-compatible.enum";

@Pipe({name: 'teslaCompatible'})
export class TeslaCompatiblePipe implements PipeTransform {
  transform(value: string | null | undefined): string {

    switch(value) {
      case null: return 'Unknown'
      case undefined: return 'Unknown'
      case TeslaCompatibleEnum[TeslaCompatibleEnum.YES]: return 'Yes'
      case TeslaCompatibleEnum[TeslaCompatibleEnum.NO]: return 'No'
      default: return 'Unknown'
    }
  }
}
