import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {PoiService} from "../../services/poi.service";
import {PoiModel} from "../../models/poi.model";
import {ChargingStationModel} from "../../models/charging-station.model";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {BooleanVariablePipe} from "../../pipes/boolean.variable.pipe";
import {MatDialog} from "@angular/material/dialog";
import {PoiAnimationDialogComponent} from "../poi-animation-dialog/poi-animation-dialog.component";
import {YesNoQuestionDialogComponent} from "../yes-no-question.dialog/yes-no-question.dialog.component";
import {DatePipe} from "../../pipes/date.pipe";
import {MapDialogComponent} from "../map-dialog/map-dialog.component";
import {ChargingStationDialogComponent} from "../charging-station-dialog/charging-station-dialog.component";
import {RolesModel} from "../../models/roles.model";

@Component({
  selector: 'app-pois',
  templateUrl: './pois.component.html',
  styleUrls: ['./pois.component.css']
})
export class PoisComponent implements AfterViewInit, OnInit {
  loggedInRoles: RolesModel[] = [];
  displayedColumns: string[] = ['location', 'parking', 'illumination', 'wc', 'shopping', 'food', 'phone', 'timestamp', 'chargingStation', 'manipulate'];
  dataSource = new MatTableDataSource<PoiModel>();

  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(private authService: AuthService,
              private poiService: PoiService,
              public booleanVariablePipe: BooleanVariablePipe,
              public dialog: MatDialog,
              public datePipe: DatePipe) {
  }
  ngOnInit() {
    //if page is refreshed wait for auth loading
    if(!this.authService.isLoggedIn){
      this.authService.isLoggedInChange.subscribe(value => {
        if(value){
          this.loggedInRoles = this.authService.loggedInUserRoles;
          this.fetchUserPois();
        }
      });
    }
    else{
      this.loggedInRoles = this.authService.loggedInUserRoles;
      this.fetchUserPois();
    }
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  hasRole(roleName: string): boolean {
    return this.loggedInRoles.some(r => roleName === r.name);
  }

  isOwner(): boolean {
    return this.hasRole('POI_OWNER');
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN')
  }

  fetchUserPois(){
    if(this.isAdmin()){
      this.poiService.getPois().subscribe({
        next: pois => {
          this.dataSource.data = pois;
        }
      });
    }
    else if(this.isOwner()){
      this.poiService.getUserPois().subscribe({
        next: pois => {
          this.dataSource.data = pois;
        }
      });
    }
  }

  showAddPoiDialog(){
    this.dialog.open(PoiAnimationDialogComponent, {
      width: '60%',
      height: '80%',
      enterAnimationDuration: 1000,
      exitAnimationDuration: 200,
    }).afterClosed().subscribe(poiAdded => {
      if(poiAdded){
        const newData = this.dataSource.data;
        newData.unshift(poiAdded);
        this.dataSource.data = newData;
      }
    });
  }

  showEditPoiDialog(currentPoi: PoiModel){
    this.dialog.open(PoiAnimationDialogComponent, {
      width: '60%',
      height: '80%',
      enterAnimationDuration: 1000,
      exitAnimationDuration: 200,
      data: currentPoi,
    }).afterClosed().subscribe(poiEdited => {
      if(poiEdited){
        const updatedData = this.dataSource.data;
        const oldPoi = updatedData.find(poi => poi.id === poiEdited.id);
        const oldPoiIndex = updatedData.indexOf(oldPoi!!);
        if(~oldPoiIndex){
          updatedData[oldPoiIndex] = poiEdited;
          this.dataSource.data = updatedData;
        }
      }
    });
  }

  deletePoi(id: string) {
    this.dialog.open(YesNoQuestionDialogComponent, {
      width: '350px',
      height: '200px',
      enterAnimationDuration: 500,
      exitAnimationDuration: 100,
      data: 'Are you sure you want to delete selected Point of Interest?'
    }).afterClosed().subscribe((userAnswer: boolean) => {
      if(userAnswer){
        this.poiService.deletePoi(id).subscribe({
          next: isDeleted => {
            if(isDeleted){
              const updatedData = this.dataSource.data;
              const removedPoi = updatedData.find(poi => poi.id === id);
              const removedPoiIndex = updatedData.indexOf(removedPoi!!);
              if(~removedPoiIndex){
                updatedData.splice(removedPoiIndex, 1); //remove one item only
                this.dataSource.data = updatedData;
              }
            }
          }
        })
      }
    });
  }

  showLocationDialog(latitude: number, longitude: number) {
    this.dialog.open(MapDialogComponent, {
      width: '50%',
      height: '70%',
      enterAnimationDuration: 500,
      exitAnimationDuration: 100,
      data: {
        latitude: latitude,
        longitude: longitude
      }
    })
  }

  showChargingStationDialog(chargingStation: ChargingStationModel) {
    this.dialog.open(ChargingStationDialogComponent, {
      width: '60%',
      height: '80%',
      enterAnimationDuration: 1000,
      exitAnimationDuration: 200,
      data: chargingStation,
    });
  }
}
