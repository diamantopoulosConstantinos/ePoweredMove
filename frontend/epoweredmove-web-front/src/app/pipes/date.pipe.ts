import {Pipe, PipeTransform} from "@angular/core";

@Pipe({name: 'date'})
export class DatePipe implements PipeTransform {
  transform(value: number | null | undefined): string {

    switch(value) {
      case null: return '-'
      case undefined: return '-'
      default: {
        const date = new Date(value);
        const year = date.getFullYear();
        const month = ("0" + (date.getMonth() + 1)).slice(-2);
        const day = ("0" + date.getDate()).slice(-2);

        return `${day}/${month}/${year}`;
      }
    }
  }
}
