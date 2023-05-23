import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {PlugService} from "../../services/plug.service";
import {PlugTypeService} from "../../services/plug.type.service";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {AlertComponent} from "../alert/alert.component";
import {PlugModel} from "../../models/plug.model";
import {PlugTypeModel} from "../../models/plug-type.model";
import {AvailabilityEnum} from "../../models/enums/availability.enum";

@Component({
  selector: 'app-plug-animation-dialog',
  templateUrl: './plug-animation-dialog.component.html',
  styleUrls: ['./plug-animation-dialog.component.css']
})
export class PlugAnimationDialogComponent implements OnInit {
  plugFormGroup: FormGroup = new FormGroup({
    power: new FormControl(null, [Validators.required, Validators.pattern('^\\d+\\.\\d{0,3}$')]),
    phases: new FormControl(null, [Validators.required, Validators.pattern("^[1-3]$")]),
    plugTypeId: new FormControl(null, [Validators.required]),
  });

  plugTypes: PlugTypeModel[] = [];
  selectedPlugTypeId: string;
  constructor(@Inject(MAT_DIALOG_DATA) private data: string,
              private plugService: PlugService,
              private plugTypeService: PlugTypeService,
              public dialogRef: MatDialogRef<PlugAnimationDialogComponent>,
              private alert: AlertComponent) {}


  ngOnInit() {
    this.getPlugTypes();
  }

  private getPlugTypes() {
    this.plugTypeService.getPlugTypes().subscribe({
      next: plugTypes => {
        if(plugTypes && plugTypes.length > 0){
          this.plugTypes = plugTypes;
          plugTypes.forEach(plugType => {
            this.getPlugTypeImage(plugType);
          })
        }
        else {
          this.alert.failureMessage("Error occurred while fetching plug types. Please try again.")
          this.dialogRef.close();
        }
      },
      error: err => {
        this.alert.failureMessage("Error occurred while fetching plug types. Please try again.")
        this.dialogRef.close();
      }
    })
  }

  private getPlugTypeImage(plugType: PlugTypeModel) {
    this.plugTypeService.getPlugTypeImage(plugType.imageId).subscribe({
      next: imageUrl => {
        if(imageUrl){
          const plugTypeIndex = this.plugTypes.indexOf(plugType);
          if(~plugTypeIndex){
            plugType.imageUrl = imageUrl;
            this.plugTypes[plugTypeIndex] = plugType;
          }
        }
      }
    })
  }

  submitPlug(){
    if(this.plugFormGroup.valid){
      const plug: PlugModel = {} as PlugModel;
      plug.chargingStationId = this.data;
      plug.availability = AvailabilityEnum[AvailabilityEnum.AVAILABLE];
      plug.phases = this.plugFormGroup.get('phases')?.value;
      plug.power = this.plugFormGroup.get('power')?.value;
      plug.plugTypeId = this.plugFormGroup.get('plugTypeId')?.value;
      this.plugService.addPlug(plug).subscribe({
        next: plugAdded => {
          if(plugAdded){
            const plugTypeSelected = this.plugTypes.find(plug => plug.id === plugAdded.plugTypeId);
            if(plugTypeSelected){
              plugAdded.plugTypeObj.imageUrl = plugTypeSelected!!.imageUrl;
            }
            this.dialogRef.close(plugAdded);
          }
          else{
            this.alert.failureMessage("Error occurred while adding plug. Please try again.");
          }
        },
        error: err => {
          this.alert.failureMessage("Error occurred while adding plug. Please try again.");
        }
      })
    }
  }
}
