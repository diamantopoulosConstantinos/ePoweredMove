import {Component, OnInit} from '@angular/core';
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent{
  constructor(public snackBar: MatSnackBar) { }

  notificationMessage(message: string) {
    this.snackBar.open(message, undefined, {
      duration: 3000,
      horizontalPosition: "center",
      verticalPosition: "top",
      panelClass: 'warning-snackbar',
    });
  }
  failureMessage(message: string) {
    this.snackBar.open(message, undefined, {
      duration: 3000,
      horizontalPosition: "center",
      verticalPosition: "top",
      panelClass: 'fail-snackbar',
    });
  }
  successMessage(message: string) {
    this.snackBar.open(message, undefined, {
      duration: 3000,
      horizontalPosition: "center",
      verticalPosition: "top",
      panelClass: 'success-snackbar',
    });
  }

  signInMessage(userName: string | null) {
    this.snackBar.open("Welcome to E-Powered Move!" + (userName? " You are logged in as " + userName:"") , undefined, {
      duration: 3000,
      horizontalPosition: "center",
      verticalPosition: "top",
      panelClass: 'info-snackbar',
    });
  }
}
