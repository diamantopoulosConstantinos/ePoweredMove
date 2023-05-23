import {Pipe, PipeTransform} from "@angular/core";

@Pipe({name: 'time'})
export class TimePipe implements PipeTransform {
  transform(value: number | null | undefined): string {

    switch(value) {
      case null: return '-'
      case undefined: return '-'
      default: {
        const date = new Date(value);
        const hour = ("0" + date.getHours()).slice(-2);
        const mins = ("0" + date.getMinutes()).slice(-2);

        return `${hour}:${mins}`;
      }
    }
  }
}
