import {Pipe, PipeTransform} from "@angular/core";

@Pipe({name: 'location'})
export class LocationPipe implements PipeTransform {
  transform(value: number): number {

    return +value.toFixed(8)
  }
}
