import {Component, Inject, OnInit} from '@angular/core';
import {PlugModel} from "../../models/plug.model";
import {PreviewPlugDialogComponent} from "../preview-plug-dialog/preview-plug-dialog.component";
import {AvailabilityPipe} from "../../pipes/availability.pipe";
import {DatePipe} from "../../pipes/date.pipe";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ChargingStationModel} from "../../models/charging-station.model";
import {PlugService} from "../../services/plug.service";
import {PlugTypeService} from "../../services/plug.type.service";
import {AlertComponent} from "../alert/alert.component";
import {ChargingStationService} from "../../services/charging.station.service";

@Component({
  selector: 'app-preview-plugs-dialog',
  templateUrl: './preview-plugs-dialog.component.html',
  styleUrls: ['./preview-plugs-dialog.component.css']
})
export class PreviewPlugsDialogComponent implements OnInit{
  currentChargingStation: ChargingStationModel;

  constructor(@Inject(MAT_DIALOG_DATA) private data: ChargingStationModel,
              public availabilityPipe: AvailabilityPipe,
              public datePipe: DatePipe,
              public dialog: MatDialog,
              private plugService: PlugService,
              private plugTypeService: PlugTypeService,
              private alert: AlertComponent,
              private chargingStationService: ChargingStationService,) {
    this.currentChargingStation = this.data;
    this.currentChargingStation.plugsObj = [];
  }

  ngOnInit() {
    this.getChargingStationPlugs();
  }

  private getChargingStationPlugs() {
    this.plugService.getPlugsByChargingStation(this.currentChargingStation.id).subscribe({
      next: plugs => {
        this.currentChargingStation.plugsObj = plugs;
        this.currentChargingStation.plugsObj.forEach(plug => {
          this.getPlugTypeImage(plug);
        })
      },
      error: err => {
        this.alert.failureMessage("Error occurred while retrieving charging station plugs.")
      }
    })
  }

  private getPlugTypeImage(plug: PlugModel) {
    if(plug.plugTypeObj && plug.plugTypeObj.imageId){
      this.plugTypeService.getPlugTypeImage(plug.plugTypeObj.imageId).subscribe({
        next: imageUrl => {
          if(imageUrl){
            const plugIndex = this.currentChargingStation.plugsObj.indexOf(plug);
            if(~plugIndex){
              const plugType = this.currentChargingStation.plugsObj[plugIndex].plugTypeObj;
              if(plugType){
                plugType.imageUrl = imageUrl;
                this.currentChargingStation.plugsObj[plugIndex].plugTypeObj = plugType;
              }
            }
          }
        }
      })
    }
  }

  showPlugDialog(plug: PlugModel) {
    this.dialog.open(PreviewPlugDialogComponent, {
      width: '50%',
      height: '70%',
      enterAnimationDuration: 1000,
      exitAnimationDuration: 200,
      data: plug.id
    });
  }
}
