import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ChargingStationModel} from "../../models/charging-station.model";
import {PlugService} from "../../services/plug.service";
import {AlertComponent} from "../alert/alert.component";
import {ChargingStationService} from "../../services/charging.station.service";
import {PlugModel} from "../../models/plug.model";
import {YesNoQuestionDialogComponent} from "../yes-no-question.dialog/yes-no-question.dialog.component";
import {BooleanVariablePipe} from "../../pipes/boolean.variable.pipe";
import {KwhPricePipe} from "../../pipes/kwh.price.pipe";
import {DatePipe} from "../../pipes/date.pipe";
import {PlugTypeService} from "../../services/plug.type.service";
import {AvailabilityPipe} from "../../pipes/availability.pipe";
import {PlugAnimationDialogComponent} from "../plug-animation-dialog/plug-animation-dialog.component";
import {AvailabilityEnum} from "../../models/enums/availability.enum";
import {PreviewPlugDialogComponent} from "../preview-plug-dialog/preview-plug-dialog.component";
import {ReviewsDialogComponent} from "../reviews-dialog/reviews-dialog.component";

@Component({
  selector: 'app-charging-station-dialog',
  templateUrl: './charging-station-dialog.component.html',
  styleUrls: ['./charging-station-dialog.component.css']
})
export class ChargingStationDialogComponent implements OnInit {
  currentChargingStation: ChargingStationModel;
  plugAvailabilities: string[] = [];
  constructor(@Inject(MAT_DIALOG_DATA) private data: ChargingStationModel,
              private plugService: PlugService,
              private plugTypeService: PlugTypeService,
              private alert: AlertComponent,
              private chargingStationService: ChargingStationService,
              public dialog: MatDialog,
              public booleanPipe: BooleanVariablePipe,
              public kwhPricePipe: KwhPricePipe,
              public datePipe: DatePipe,
              public availabilityPipe: AvailabilityPipe) {
    this.currentChargingStation = this.data;
    this.currentChargingStation.plugsObj = [];
    for (let availability in AvailabilityEnum) {
      if (isNaN(Number(availability))) {
        this.plugAvailabilities.push(availability);
      }
    }
  }

  ngOnInit() {
    this.getChargingStationImage();
    this.getChargingStationPlugs();
  }

  private getChargingStationImage() {
    if(this.currentChargingStation && this.currentChargingStation.imageId){
      this.chargingStationService.getChargingStationImage(this.currentChargingStation.imageId).subscribe({
        next: imageUrl => {
          if(imageUrl){
            this.currentChargingStation.imageUrl  = imageUrl;
          }
        }
      })
    }
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


  showAddPlugDialog(){
    this.dialog.open(PlugAnimationDialogComponent, {
      width: '50%',
      height: '70%',
      enterAnimationDuration: 1000,
      exitAnimationDuration: 200,
      data: this.currentChargingStation.id
    }).afterClosed().subscribe(plugAdded => {
      if(plugAdded){
        this.currentChargingStation.plugsObj.push(plugAdded);
      }
    });
  }

  deletePlug(plugId: string){
    this.dialog.open(YesNoQuestionDialogComponent, {
      width: '350px',
      height: '200px',
      enterAnimationDuration: 500,
      exitAnimationDuration: 100,
      data: 'Are you sure you want to delete selected Plug?'
    }).afterClosed().subscribe((userAnswer: boolean) => {
      if(userAnswer){
        this.plugService.deletePlug(plugId).subscribe({
          next: isDeleted => {
            if(isDeleted){
              const updatedData = this.currentChargingStation.plugsObj;
              const removedPlug = updatedData.find(plug => plug.id === plugId);
              const removedPlugIndex = updatedData.indexOf(removedPlug!!);
              if(~removedPlugIndex){
                updatedData.splice(removedPlugIndex, 1); //remove one item only
                this.currentChargingStation.plugsObj = updatedData;
              }
            }
          }
        })
      }
    });
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

  changeAvailability(plug: PlugModel) {
    this.plugService.updatePlug(plug).subscribe();
  }

  showReviewsDialog() {
    this.dialog.open(ReviewsDialogComponent, {
      width: '50%',
      height: '70%',
      enterAnimationDuration: 1000,
      exitAnimationDuration: 200,
      data: this.currentChargingStation.id
    });
  }
}
