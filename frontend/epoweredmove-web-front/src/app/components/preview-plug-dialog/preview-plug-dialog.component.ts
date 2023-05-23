import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ChargingStationModel} from "../../models/charging-station.model";
import {PlugModel} from "../../models/plug.model";
import {AlertComponent} from "../alert/alert.component";
import {AvailabilityEnum} from "../../models/enums/availability.enum";
import {AvailabilityPipe} from "../../pipes/availability.pipe";
import {DatePipe} from "../../pipes/date.pipe";
import {BooleanVariablePipe} from "../../pipes/boolean.variable.pipe";
import {TeslaCompatiblePipe} from "../../pipes/tesla.compatible.pipe";
import {CurrentTypePipe} from "../../pipes/current.type.pipe";
import {PlugService} from "../../services/plug.service";
import {PlugTypeService} from "../../services/plug.type.service";

@Component({
  selector: 'app-preview-plug-dialog',
  templateUrl: './preview-plug-dialog.component.html',
  styleUrls: ['./preview-plug-dialog.component.css']
})
export class PreviewPlugDialogComponent implements OnInit {
  plug: PlugModel;
  constructor(@Inject(MAT_DIALOG_DATA) private data: string,
              public dialogRef: MatDialogRef<PreviewPlugDialogComponent>,
              private alert: AlertComponent,
              public availabilityPipe: AvailabilityPipe,
              public datePipe: DatePipe,
              public teslaCompatiblePipe: TeslaCompatiblePipe,
              public currentTypePipe: CurrentTypePipe,
              private plugService: PlugService,
              private plugTypeService: PlugTypeService) {
  }

  ngOnInit() {
    if(this.data){
      this.plugService.getPlug(this.data).subscribe({
        next: plug => {
          if(plug){
            this.plug = plug;
            this.getPlugTypeImage();
          }
          else{
            this.alert.failureMessage("Something went wrong. Please try again");
            this.dialogRef.close();
          }
        },
        error: err => {
          this.alert.failureMessage("Something went wrong. Please try again");
          this.dialogRef.close();
        }
      })
    }
    else{
      this.alert.failureMessage("Something went wrong. Please try again");
      this.dialogRef.close();
    }

  }

  private getPlugTypeImage() {
    if(this.plug && this.plug.plugTypeObj && this.plug.plugTypeObj.imageId){
      this.plugTypeService.getPlugTypeImage(this.plug.plugTypeObj.imageId).subscribe({
        next: imageUrl => {
          if(imageUrl){
            this.plug.plugTypeObj.imageUrl = imageUrl;
          }
        }
      });
    }
  }
}
