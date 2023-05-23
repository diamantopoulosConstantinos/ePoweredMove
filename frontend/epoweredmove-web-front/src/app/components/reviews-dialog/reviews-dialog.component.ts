import {AfterViewInit, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ChargingStationModel} from "../../models/charging-station.model";
import {ReviewService} from "../../services/review.service";
import {AlertComponent} from "../alert/alert.component";
import {ReviewModel} from "../../models/review.model";
import {MatTableDataSource} from "@angular/material/table";
import {PoiModel} from "../../models/poi.model";
import {MatPaginator} from "@angular/material/paginator";
import {DatePipe} from "../../pipes/date.pipe";
import {ReviewStatusPipe} from "../../pipes/review.status.pipe";

@Component({
  selector: 'app-reviews-dialog',
  templateUrl: './reviews-dialog.component.html',
  styleUrls: ['./reviews-dialog.component.css']
})
export class ReviewsDialogComponent implements OnInit, AfterViewInit{
  displayedColumns: string[] = ['rating', 'comments', 'user', 'timestamp', 'status'];
  dataSource = new MatTableDataSource<ReviewModel>();

  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(@Inject(MAT_DIALOG_DATA) private data: string,
              private reviewService: ReviewService,
              private dialogRef: MatDialogRef<ReviewsDialogComponent>,
              private alert: AlertComponent,
              public datePipe: DatePipe,
              public reviewStatusPipe: ReviewStatusPipe) {
  }

  ngOnInit() {
    if(this.data){
      this.reviewService.getReviewsByChargingStation(this.data).subscribe({
        next: reviews => {
          this.dataSource.data = reviews;
        },
        error: err => {
          this.alert.failureMessage("Error occurred while fetching reviews");
          this.dialogRef.close();
        }
      })
    }
    else{
      this.alert.failureMessage("Error occurred while fetching reviews");
      this.dialogRef.close();
    }
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
}
