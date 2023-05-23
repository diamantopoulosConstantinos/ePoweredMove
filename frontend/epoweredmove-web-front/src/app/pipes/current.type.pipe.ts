import {Pipe, PipeTransform} from "@angular/core";
import {TeslaCompatibleEnum} from "../models/enums/tesla-compatible.enum";
import {CurrentTypeEnum} from "../models/enums/current-type.enum";

@Pipe({name: 'currentType'})
export class CurrentTypePipe implements PipeTransform {
  transform(value: string | null | undefined): string {

    switch(value) {
      case null: return 'Unknown'
      case undefined: return 'Unknown'
      case CurrentTypeEnum[CurrentTypeEnum.AC]: return 'AC'
      case CurrentTypeEnum[CurrentTypeEnum.DC]: return 'DC'
      default: return 'Unknown'
    }
  }
}
