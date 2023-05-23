import {Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {PoiModel} from "../../models/poi.model";
import {MatPaginator} from "@angular/material/paginator";
import {AuthService} from "../../services/auth.service";
import {PoiService} from "../../services/poi.service";
import {BooleanVariablePipe} from "../../pipes/boolean.variable.pipe";
import {MatDialog} from "@angular/material/dialog";
import {DatePipe} from "../../pipes/date.pipe";
import {ReservationModel} from "../../models/reservation.model";
import {ReservationService} from "../../services/reservation.service";
import {AlertComponent} from "../alert/alert.component";
import {TimePipe} from "../../pipes/time.pipe";
import {PlugModel} from "../../models/plug.model";
import {PreviewPlugDialogComponent} from "../preview-plug-dialog/preview-plug-dialog.component";
import {RolesModel} from "../../models/roles.model";

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
  styleUrls: ['./reservations.component.css']
})
export class ReservationsComponent implements OnInit{
  loggedInRoles: RolesModel[] = [];
  displayedColumns: string[] = ['plug', 'dateOfReservation', 'timeStart', 'timeEnd', 'accepted', 'cancelled', 'user', 'timestamp'];
  dataSource = new MatTableDataSource<ReservationModel>();

  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(public booleanVariablePipe: BooleanVariablePipe,
              public dialog: MatDialog,
              public datePipe: DatePipe,
              private reservationService: ReservationService,
              private alert: AlertComponent,
              public timePipe: TimePipe,
              public booleanPipe: BooleanVariablePipe,
              private authService: AuthService) {
  }

  ngOnInit() {
    //if page is refreshed wait for auth loading
    if(!this.authService.isLoggedIn){
      this.authService.isLoggedInChange.subscribe(value => {
        if(value){
          this.loggedInRoles = this.authService.loggedInUserRoles;
          this.fetchReservations();
        }
      });
    }
    else{
      this.loggedInRoles = this.authService.loggedInUserRoles;
      this.fetchReservations();
    }
  }

  private fetchReservations() {
    if(this.isAdmin()){
      this.reservationService.getReservations().subscribe({
        next: reservations => {
          this.dataSource.data = reservations;
        },
        error: err => {
          this.alert.failureMessage("Error occurred while fetching reservations.");
        }
      });
    }
    else{
      this.reservationService.getReservationsByChargingStationOwner().subscribe({
        next: reservations => {
          this.dataSource.data = reservations;
        },
        error: err => {
          this.alert.failureMessage("Error occurred while fetching reservations.");
        }
      });
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

  changeReservationAccepted(reservation: ReservationModel) {
    this.reservationService.updateReservation(reservation).subscribe();
  }

  hasRole(roleName: string): boolean {
    return this.loggedInRoles.some(r => roleName === r.name);
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN')
  }
}
