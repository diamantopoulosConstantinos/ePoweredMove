import {Pipe, PipeTransform} from "@angular/core";

@Pipe({name: 'kwhPrice'})
export class KwhPricePipe implements PipeTransform {
  transform(value: number | null | undefined): string | null {

    switch(value) {
      case null: return null
      case undefined: return null
      default: return value.toFixed(3)
    }
  }
}
