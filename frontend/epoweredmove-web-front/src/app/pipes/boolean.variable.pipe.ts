import {Pipe, PipeTransform} from "@angular/core";

@Pipe({name: 'booleanVariable'})
export class BooleanVariablePipe implements PipeTransform {
  transform(value: boolean | null | undefined): string {

    switch(value) {
      case null: return 'N/A'
      case undefined: return 'N/A'
      case true: return 'Yes'
      case false: return 'No'
      default: return '-'
    }
  }
}
